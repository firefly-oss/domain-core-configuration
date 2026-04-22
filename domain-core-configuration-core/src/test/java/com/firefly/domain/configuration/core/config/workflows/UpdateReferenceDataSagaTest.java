package com.firefly.domain.configuration.core.config.workflows;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataRegistry;
import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataValidationException;
import com.firefly.domain.configuration.core.config.workflows.commands.CaptureEntitySnapshotSagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.ReapplyPreviousStateSagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.UpdateLocalizationSagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.UpdateReferenceDataSagaCommand;
import com.firefly.domain.configuration.interfaces.rest.reference.LocalizationPayload;
import org.fireflyframework.cqrs.command.Command;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static com.firefly.domain.configuration.core.utils.constants.ReferenceDataConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateReferenceDataSagaTest {

    private CommandBus commandBus;
    private ReferenceDataRegistry registry;
    private UpdateReferenceDataSaga saga;

    @BeforeEach
    void setUp() {
        commandBus = mock(CommandBus.class);
        registry = mock(ReferenceDataRegistry.class);
        when(registry.contains(any(ReferenceDataEntityType.class))).thenReturn(true);
        saga = new UpdateReferenceDataSaga(commandBus, registry);
    }

    @Test
    void captureSnapshot_storesCtxAndReturnsSnapshot() {
        Object snapshot = new Object();
        AtomicReference<Command<?>> dispatched = new AtomicReference<>();
        when(commandBus.send(any(Command.class))).thenAnswer(inv -> {
            dispatched.set(inv.getArgument(0));
            return Mono.just(snapshot);
        });

        UUID entityId = UUID.randomUUID();
        Object payload = new Object();
        UpdateReferenceDataSagaCommand cmd = UpdateReferenceDataSagaCommand.builder()
                .entityType(ReferenceDataEntityType.CURRENCY)
                .entityId(entityId)
                .payload(payload)
                .localizations(List.of())
                .build();
        ExecutionContext ctx = ExecutionContext.forSaga("corr", SAGA_UPDATE_REFERENCE_DATA);

        StepVerifier.create(saga.captureSnapshot(cmd, ctx))
                .expectNext(snapshot)
                .verifyComplete();

        assertThat(dispatched.get()).isInstanceOf(CaptureEntitySnapshotSagaCommand.class);
        assertThat(ctx.getVariableAs(CTX_ENTITY_TYPE, ReferenceDataEntityType.class))
                .isEqualTo(ReferenceDataEntityType.CURRENCY);
        assertThat(ctx.getVariableAs(CTX_PRIMARY_ENTITY_ID, UUID.class)).isEqualTo(entityId);
        assertThat(ctx.getVariableAs(CTX_PREVIOUS_STATE, Object.class)).isSameAs(snapshot);
    }

    @Test
    void captureSnapshot_failsFast_onUnsupportedEntityType() {
        when(registry.contains(any(ReferenceDataEntityType.class))).thenReturn(false);
        UpdateReferenceDataSagaCommand cmd = UpdateReferenceDataSagaCommand.builder()
                .entityType(ReferenceDataEntityType.CURRENCY)
                .entityId(UUID.randomUUID())
                .payload(new Object())
                .build();

        StepVerifier.create(saga.captureSnapshot(cmd, ExecutionContext.forSaga("corr", SAGA_UPDATE_REFERENCE_DATA)))
                .expectError(ReferenceDataValidationException.class)
                .verify();
    }

    @Test
    void updatePrimaryEntity_dispatchesUpdateCommandFromCtxData() {
        UUID entityId = UUID.randomUUID();
        Object payload = new Object();
        ExecutionContext ctx = ExecutionContext.forSaga("corr", SAGA_UPDATE_REFERENCE_DATA);
        ctx.putVariable(CTX_ENTITY_TYPE, ReferenceDataEntityType.CURRENCY);
        ctx.putVariable(CTX_PRIMARY_ENTITY_ID, entityId);
        ctx.putVariable("payload", payload);

        AtomicReference<Command<?>> dispatched = new AtomicReference<>();
        when(commandBus.send(any(Command.class))).thenAnswer(inv -> {
            dispatched.set(inv.getArgument(0));
            return Mono.just(entityId);
        });

        StepVerifier.create(saga.updatePrimaryEntity(ctx))
                .expectNext(entityId)
                .verifyComplete();

        assertThat(dispatched.get()).isInstanceOf(UpdateReferenceDataSagaCommand.class);
        UpdateReferenceDataSagaCommand sent = (UpdateReferenceDataSagaCommand) dispatched.get();
        assertThat(sent.getEntityType()).isEqualTo(ReferenceDataEntityType.CURRENCY);
        assertThat(sent.getEntityId()).isEqualTo(entityId);
        assertThat(sent.getPayload()).isSameAs(payload);
    }

    @Test
    void updateLocalizations_returnsSentinel_whenListIsEmpty() {
        ExecutionContext ctx = ExecutionContext.forSaga("corr", SAGA_UPDATE_REFERENCE_DATA);
        ctx.putVariable("localizationsRequested", List.of());
        ctx.putVariable(CTX_PREVIOUS_LOCALIZATIONS, new java.util.ArrayList<LocalizationPayload>());

        StepVerifier.create(saga.updateLocalizations(ctx))
                .expectNext(SENTINEL_NO_LOCALIZATIONS)
                .verifyComplete();
    }

    @Test
    void updateLocalizations_dispatchesOnePerPayload() {
        ExecutionContext ctx = ExecutionContext.forSaga("corr", SAGA_UPDATE_REFERENCE_DATA);
        ctx.putVariable(CTX_ENTITY_TYPE, ReferenceDataEntityType.CURRENCY);
        ctx.putVariable(CTX_PRIMARY_ENTITY_ID, UUID.randomUUID());
        List<LocalizationPayload> localizations = List.of(
                LocalizationPayload.builder().localeTag("es-ES").languageLocaleId(UUID.randomUUID()).build(),
                LocalizationPayload.builder().localeTag("en-GB").languageLocaleId(UUID.randomUUID()).build()
        );
        ctx.putVariable("localizationsRequested", localizations);
        ctx.putVariable(CTX_PREVIOUS_LOCALIZATIONS, new java.util.ArrayList<LocalizationPayload>());

        java.util.concurrent.atomic.AtomicInteger count = new java.util.concurrent.atomic.AtomicInteger();
        when(commandBus.send(any(Command.class))).thenAnswer(inv -> {
            count.incrementAndGet();
            return Mono.just(UUID.randomUUID());
        });

        StepVerifier.create(saga.updateLocalizations(ctx))
                .assertNext(result -> assertThat(result).isInstanceOf(List.class))
                .verifyComplete();

        assertThat(count.get()).isEqualTo(2);
    }

    @Test
    void restorePrimaryEntity_sendsReapplyCommand() {
        UUID entityId = UUID.randomUUID();
        Object snapshot = new Object();
        ExecutionContext ctx = ExecutionContext.forSaga("corr", SAGA_UPDATE_REFERENCE_DATA);
        ctx.putVariable(CTX_ENTITY_TYPE, ReferenceDataEntityType.CURRENCY);
        ctx.putVariable(CTX_PRIMARY_ENTITY_ID, entityId);
        ctx.putVariable(CTX_PREVIOUS_STATE, snapshot);

        AtomicReference<Command<?>> dispatched = new AtomicReference<>();
        when(commandBus.send(any(Command.class))).thenAnswer(inv -> {
            dispatched.set(inv.getArgument(0));
            return Mono.empty();
        });

        StepVerifier.create(saga.restorePrimaryEntity(ctx)).verifyComplete();
        assertThat(dispatched.get()).isInstanceOf(ReapplyPreviousStateSagaCommand.class);
        ReapplyPreviousStateSagaCommand sent = (ReapplyPreviousStateSagaCommand) dispatched.get();
        assertThat(sent.getEntityId()).isEqualTo(entityId);
        assertThat(sent.getPreviousState()).isSameAs(snapshot);
    }

    @Test
    void restorePrimaryEntity_isNoOp_whenSnapshotMissing() {
        ExecutionContext ctx = ExecutionContext.forSaga("corr", SAGA_UPDATE_REFERENCE_DATA);
        ctx.putVariable(CTX_ENTITY_TYPE, ReferenceDataEntityType.CURRENCY);
        ctx.putVariable(CTX_PRIMARY_ENTITY_ID, UUID.randomUUID());

        StepVerifier.create(saga.restorePrimaryEntity(ctx)).verifyComplete();
    }

    @Test
    void restoreLocalizations_reappliesEachPreviousPayload() {
        ExecutionContext ctx = ExecutionContext.forSaga("corr", SAGA_UPDATE_REFERENCE_DATA);
        ctx.putVariable(CTX_ENTITY_TYPE, ReferenceDataEntityType.CURRENCY);
        ctx.putVariable(CTX_PRIMARY_ENTITY_ID, UUID.randomUUID());
        List<LocalizationPayload> previous = new java.util.ArrayList<>(List.of(
                LocalizationPayload.builder()
                        .localizationId(UUID.randomUUID()).localeTag("es-ES")
                        .languageLocaleId(UUID.randomUUID()).build()));
        ctx.putVariable(CTX_PREVIOUS_LOCALIZATIONS, previous);

        java.util.concurrent.atomic.AtomicInteger count = new java.util.concurrent.atomic.AtomicInteger();
        when(commandBus.send(any(Command.class))).thenAnswer(inv -> {
            count.incrementAndGet();
            Command<?> c = inv.getArgument(0);
            assertThat(c).isInstanceOf(UpdateLocalizationSagaCommand.class);
            return Mono.just(UUID.randomUUID());
        });

        StepVerifier.create(saga.restoreLocalizations(ctx)).verifyComplete();
        assertThat(count.get()).isEqualTo(1);
    }
}
