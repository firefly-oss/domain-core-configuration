package com.firefly.domain.configuration.core.config.workflows;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataRegistry;
import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataValidationException;
import com.firefly.domain.configuration.core.config.workflows.commands.CaptureEntitySnapshotSagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.ReapplyPreviousStateSagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.UpdateLocalizationSagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.UpdateReferenceDataSagaCommand;
import com.firefly.domain.configuration.interfaces.rest.reference.LocalizationPayload;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.firefly.domain.configuration.core.utils.constants.ReferenceDataConstants.*;

/**
 * Atomically updates a reference-data entity and its dependent localizations.
 * On failure the saga rolls back by reapplying the snapshot captured in step 1
 * (primary entity) and reissuing the previous localization payloads.
 *
 * Context variable flow:
 * <pre>
 *   STEP_CAPTURE_SNAPSHOT      -> CTX_ENTITY_TYPE, CTX_PRIMARY_ENTITY_ID,
 *                                 CTX_PREVIOUS_STATE, CTX_PREVIOUS_LOCALIZATIONS (empty init)
 *   STEP_UPDATE_PRIMARY_ENTITY -> no context writes (reads CTX_PREVIOUS_STATE)
 *   STEP_UPDATE_LOCALIZATIONS  -> appends to CTX_PREVIOUS_LOCALIZATIONS
 *   compensations              -> read CTX_PREVIOUS_STATE / CTX_PREVIOUS_LOCALIZATIONS
 * </pre>
 */
@Slf4j
@Saga(name = SAGA_UPDATE_REFERENCE_DATA)
@Service
public class UpdateReferenceDataSaga {

    private static final String CTX_PAYLOAD = "payload";
    private static final String CTX_LOCALIZATIONS_REQUESTED = "localizationsRequested";

    private final CommandBus commandBus;
    private final ReferenceDataRegistry registry;

    public UpdateReferenceDataSaga(CommandBus commandBus, ReferenceDataRegistry registry) {
        this.commandBus = commandBus;
        this.registry = registry;
    }

    @SagaStep(id = STEP_CAPTURE_SNAPSHOT)
    public Mono<Object> captureSnapshot(UpdateReferenceDataSagaCommand cmd, ExecutionContext ctx) {
        if (cmd == null || cmd.getEntityType() == null || cmd.getEntityId() == null) {
            return Mono.error(new ReferenceDataValidationException(
                    "UpdateReferenceDataSagaCommand requires entityType and entityId"));
        }
        if (!registry.contains(cmd.getEntityType())) {
            return Mono.error(new ReferenceDataValidationException(
                    "Unsupported entity type: " + cmd.getEntityType()));
        }
        List<LocalizationPayload> localizations = cmd.getLocalizations() != null
                ? cmd.getLocalizations()
                : Collections.emptyList();
        ctx.putVariable(CTX_ENTITY_TYPE, cmd.getEntityType());
        ctx.putVariable(CTX_PRIMARY_ENTITY_ID, cmd.getEntityId());
        ctx.putVariable(CTX_PAYLOAD, cmd.getPayload());
        ctx.putVariable(CTX_LOCALIZATIONS_REQUESTED, localizations);
        ctx.putVariable(CTX_PREVIOUS_LOCALIZATIONS, new ArrayList<LocalizationPayload>());
        return commandBus.send(new CaptureEntitySnapshotSagaCommand(cmd.getEntityType(), cmd.getEntityId()))
                .doOnNext(snapshot -> ctx.putVariable(CTX_PREVIOUS_STATE, snapshot));
    }

    @SagaStep(id = STEP_UPDATE_PRIMARY_ENTITY,
            compensate = "restorePrimaryEntity",
            dependsOn = STEP_CAPTURE_SNAPSHOT)
    public Mono<UUID> updatePrimaryEntity(ExecutionContext ctx) {
        ReferenceDataEntityType entityType = ctx.getVariableAs(CTX_ENTITY_TYPE, ReferenceDataEntityType.class);
        UUID entityId = ctx.getVariableAs(CTX_PRIMARY_ENTITY_ID, UUID.class);
        Object payload = ctx.getVariableAs(CTX_PAYLOAD, Object.class);
        UpdateReferenceDataSagaCommand cmd = UpdateReferenceDataSagaCommand.builder()
                .entityType(entityType)
                .entityId(entityId)
                .payload(payload)
                .build();
        return commandBus.send(cmd);
    }

    @SagaStep(id = STEP_UPDATE_LOCALIZATIONS,
            compensate = "restoreLocalizations",
            dependsOn = STEP_UPDATE_PRIMARY_ENTITY)
    public Mono<Object> updateLocalizations(ExecutionContext ctx) {
        @SuppressWarnings("unchecked")
        List<LocalizationPayload> requested = (List<LocalizationPayload>)
                ctx.getVariableAs(CTX_LOCALIZATIONS_REQUESTED, List.class);
        if (requested == null || requested.isEmpty()) {
            return Mono.just(SENTINEL_NO_LOCALIZATIONS);
        }
        ReferenceDataEntityType entityType = ctx.getVariableAs(CTX_ENTITY_TYPE, ReferenceDataEntityType.class);
        UUID entityId = ctx.getVariableAs(CTX_PRIMARY_ENTITY_ID, UUID.class);
        @SuppressWarnings("unchecked")
        List<LocalizationPayload> previous = (List<LocalizationPayload>)
                ctx.getVariableAs(CTX_PREVIOUS_LOCALIZATIONS, List.class);
        return Flux.fromIterable(requested)
                .concatMap(payload -> {
                    if (payload.getLocalizationId() != null) {
                        previous.add(payload);
                    }
                    return commandBus.send(new UpdateLocalizationSagaCommand(entityType, entityId, payload));
                })
                .collectList()
                .map(list -> (Object) list);
    }

    public Mono<Void> restorePrimaryEntity(ExecutionContext ctx) {
        ReferenceDataEntityType entityType = ctx.getVariableAs(CTX_ENTITY_TYPE, ReferenceDataEntityType.class);
        UUID entityId = ctx.getVariableAs(CTX_PRIMARY_ENTITY_ID, UUID.class);
        Object snapshot = ctx.getVariableAs(CTX_PREVIOUS_STATE, Object.class);
        if (snapshot == null || entityType == null || entityId == null) {
            log.error("saga.compensation.invariant-violation saga={} step={} entityType={} entityId={} hasSnapshot={} — compensation cannot run, entity may be in inconsistent state",
                    SAGA_UPDATE_REFERENCE_DATA, STEP_UPDATE_PRIMARY_ENTITY, entityType, entityId, snapshot != null);
            return Mono.empty();
        }
        return commandBus.send(new ReapplyPreviousStateSagaCommand(entityType, entityId, snapshot))
                .then();
    }

    public Mono<Void> restoreLocalizations(ExecutionContext ctx) {
        @SuppressWarnings("unchecked")
        List<LocalizationPayload> previous = (List<LocalizationPayload>)
                ctx.getVariableAs(CTX_PREVIOUS_LOCALIZATIONS, List.class);
        if (previous == null || previous.isEmpty()) {
            return Mono.empty();
        }
        ReferenceDataEntityType entityType = ctx.getVariableAs(CTX_ENTITY_TYPE, ReferenceDataEntityType.class);
        UUID entityId = ctx.getVariableAs(CTX_PRIMARY_ENTITY_ID, UUID.class);
        return Flux.fromIterable(previous)
                .concatMap(payload -> commandBus.send(new UpdateLocalizationSagaCommand(entityType, entityId, payload)))
                .then();
    }
}
