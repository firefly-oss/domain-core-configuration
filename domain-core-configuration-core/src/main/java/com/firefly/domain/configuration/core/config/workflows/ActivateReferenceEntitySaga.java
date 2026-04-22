package com.firefly.domain.configuration.core.config.workflows;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataRegistry;
import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataConflictException;
import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataValidationException;
import com.firefly.domain.configuration.core.config.workflows.commands.ActivateReferenceEntitySagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.CaptureEntitySnapshotSagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.ReapplyPreviousStateSagaCommand;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.firefly.domain.configuration.core.utils.constants.ReferenceDataConstants.*;

/**
 * Activates or deactivates a reference-data entity after evaluating cross-entity
 * preconditions. Deactivating a CURRENCY, LEGAL_FORM, or COUNTRY that is still
 * referenced by an active product is rejected with HTTP 409 semantic.
 *
 * Context variable flow:
 * <pre>
 *   STEP_VALIDATE_CROSS_ENTITY -> CTX_ENTITY_TYPE, CTX_PRIMARY_ENTITY_ID,
 *                                 CTX_CROSS_CHECK_PASSED, CTX_TARGET_STATUS
 *   STEP_CAPTURE_SNAPSHOT      -> CTX_PREVIOUS_STATE
 *   STEP_APPLY_STATUS_CHANGE   -> reads CTX_PREVIOUS_STATE for compensation
 *   compensation               -> reapplies snapshot
 * </pre>
 */
@Slf4j
@Saga(name = SAGA_ACTIVATE_REFERENCE_ENTITY)
@Service
public class ActivateReferenceEntitySaga {

    private static final String CTX_TARGET_STATUS = "targetStatus";

    private final CommandBus commandBus;
    private final CrossEntityValidator crossEntityValidator;
    private final ReferenceDataRegistry registry;

    public ActivateReferenceEntitySaga(CommandBus commandBus,
                                       CrossEntityValidator crossEntityValidator,
                                       ReferenceDataRegistry registry) {
        this.commandBus = commandBus;
        this.crossEntityValidator = crossEntityValidator;
        this.registry = registry;
    }

    @SagaStep(id = STEP_VALIDATE_CROSS_ENTITY)
    public Mono<Object> validateCrossEntity(ActivateReferenceEntitySagaCommand cmd, ExecutionContext ctx) {
        if (cmd == null || cmd.getEntityType() == null || cmd.getEntityId() == null || cmd.getTargetStatus() == null) {
            return Mono.error(new ReferenceDataValidationException(
                    "ActivateReferenceEntitySagaCommand requires entityType, entityId and targetStatus"));
        }
        if (!registry.contains(cmd.getEntityType())) {
            return Mono.error(new ReferenceDataValidationException(
                    "Unsupported entity type: " + cmd.getEntityType()));
        }
        ctx.putVariable(CTX_ENTITY_TYPE, cmd.getEntityType());
        ctx.putVariable(CTX_PRIMARY_ENTITY_ID, cmd.getEntityId());
        ctx.putVariable(CTX_TARGET_STATUS, cmd.getTargetStatus());
        boolean deactivating = cmd.getTargetStatus() == ActivateReferenceEntitySagaCommand.TargetStatus.INACTIVE;
        if (!deactivating || !crossEntityValidator.hasCrossCheck(cmd.getEntityType())) {
            ctx.putVariable(CTX_CROSS_CHECK_PASSED, true);
            return Mono.just(SENTINEL_NO_CROSS_CHECK);
        }
        return crossEntityValidator.canDeactivate(cmd.getEntityType(), cmd.getEntityId())
                .flatMap(canDeactivate -> {
                    ctx.putVariable(CTX_CROSS_CHECK_PASSED, canDeactivate);
                    if (!canDeactivate) {
                        return Mono.error(new ReferenceDataConflictException(
                                cmd.getEntityType() + " " + cmd.getEntityId()
                                        + " is referenced by active products — deactivation blocked"));
                    }
                    return Mono.just((Object) SENTINEL_CROSS_CHECK_PASSED);
                });
    }

    @SagaStep(id = STEP_CAPTURE_SNAPSHOT, dependsOn = STEP_VALIDATE_CROSS_ENTITY)
    public Mono<Object> captureSnapshot(ExecutionContext ctx) {
        ReferenceDataEntityType entityType = ctx.getVariableAs(CTX_ENTITY_TYPE, ReferenceDataEntityType.class);
        UUID entityId = ctx.getVariableAs(CTX_PRIMARY_ENTITY_ID, UUID.class);
        return commandBus.send(new CaptureEntitySnapshotSagaCommand(entityType, entityId))
                .doOnNext(snapshot -> ctx.putVariable(CTX_PREVIOUS_STATE, snapshot));
    }

    @SagaStep(id = STEP_APPLY_STATUS_CHANGE,
            compensate = "revertStatusChange",
            dependsOn = STEP_CAPTURE_SNAPSHOT)
    public Mono<UUID> applyStatusChange(ExecutionContext ctx) {
        ReferenceDataEntityType entityType = ctx.getVariableAs(CTX_ENTITY_TYPE, ReferenceDataEntityType.class);
        UUID entityId = ctx.getVariableAs(CTX_PRIMARY_ENTITY_ID, UUID.class);
        ActivateReferenceEntitySagaCommand.TargetStatus target =
                ctx.getVariableAs(CTX_TARGET_STATUS, ActivateReferenceEntitySagaCommand.TargetStatus.class);
        ActivateReferenceEntitySagaCommand dispatch = ActivateReferenceEntitySagaCommand.builder()
                .entityType(entityType)
                .entityId(entityId)
                .targetStatus(target)
                .build();
        return commandBus.send(dispatch);
    }

    public Mono<Void> revertStatusChange(ExecutionContext ctx) {
        ReferenceDataEntityType entityType = ctx.getVariableAs(CTX_ENTITY_TYPE, ReferenceDataEntityType.class);
        UUID entityId = ctx.getVariableAs(CTX_PRIMARY_ENTITY_ID, UUID.class);
        Object snapshot = ctx.getVariableAs(CTX_PREVIOUS_STATE, Object.class);
        if (snapshot == null || entityType == null || entityId == null) {
            log.error("saga.compensation.invariant-violation saga={} step={} entityType={} entityId={} hasSnapshot={} — compensation cannot run, entity may be in inconsistent state",
                    SAGA_ACTIVATE_REFERENCE_ENTITY, STEP_APPLY_STATUS_CHANGE, entityType, entityId, snapshot != null);
            return Mono.empty();
        }
        return commandBus.send(new ReapplyPreviousStateSagaCommand(entityType, entityId, snapshot))
                .then();
    }
}
