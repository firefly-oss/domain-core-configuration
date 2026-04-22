package com.firefly.domain.configuration.core.config.workflows;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataRegistry;
import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataConflictException;
import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataValidationException;
import com.firefly.domain.configuration.core.config.workflows.commands.ActivateReferenceEntitySagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.ActivateReferenceEntitySagaCommand.TargetStatus;
import com.firefly.domain.configuration.core.config.workflows.commands.CaptureEntitySnapshotSagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.ReapplyPreviousStateSagaCommand;
import org.fireflyframework.cqrs.command.Command;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static com.firefly.domain.configuration.core.utils.constants.ReferenceDataConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActivateReferenceEntitySagaTest {

    private CommandBus commandBus;
    private CrossEntityValidator validator;
    private ReferenceDataRegistry registry;
    private ActivateReferenceEntitySaga saga;

    @BeforeEach
    void setUp() {
        commandBus = mock(CommandBus.class);
        validator = mock(CrossEntityValidator.class);
        registry = mock(ReferenceDataRegistry.class);
        when(registry.contains(any(ReferenceDataEntityType.class))).thenReturn(true);
        saga = new ActivateReferenceEntitySaga(commandBus, validator, registry);
    }

    @Test
    void validateCrossEntity_returnsSentinel_whenActivating() {
        UUID entityId = UUID.randomUUID();
        ActivateReferenceEntitySagaCommand cmd = ActivateReferenceEntitySagaCommand.builder()
                .entityType(ReferenceDataEntityType.CURRENCY)
                .entityId(entityId)
                .targetStatus(TargetStatus.ACTIVE)
                .build();
        ExecutionContext ctx = ExecutionContext.forSaga("corr", SAGA_ACTIVATE_REFERENCE_ENTITY);

        StepVerifier.create(saga.validateCrossEntity(cmd, ctx))
                .expectNext(SENTINEL_NO_CROSS_CHECK)
                .verifyComplete();
        assertThat(ctx.getVariableAs(CTX_CROSS_CHECK_PASSED, Boolean.class)).isTrue();
        assertThat(ctx.getVariableAs(CTX_PRIMARY_ENTITY_ID, UUID.class)).isEqualTo(entityId);
    }

    @Test
    void validateCrossEntity_skipsCheckForEntityWithoutCrossValidation() {
        UUID entityId = UUID.randomUUID();
        when(validator.hasCrossCheck(ReferenceDataEntityType.BANK)).thenReturn(false);
        ActivateReferenceEntitySagaCommand cmd = ActivateReferenceEntitySagaCommand.builder()
                .entityType(ReferenceDataEntityType.BANK)
                .entityId(entityId)
                .targetStatus(TargetStatus.INACTIVE)
                .build();
        ExecutionContext ctx = ExecutionContext.forSaga("corr", SAGA_ACTIVATE_REFERENCE_ENTITY);

        StepVerifier.create(saga.validateCrossEntity(cmd, ctx))
                .expectNext(SENTINEL_NO_CROSS_CHECK)
                .verifyComplete();
    }

    @Test
    void validateCrossEntity_passes_whenDeactivationIsSafe() {
        UUID entityId = UUID.randomUUID();
        when(validator.hasCrossCheck(ReferenceDataEntityType.CURRENCY)).thenReturn(true);
        when(validator.canDeactivate(eq(ReferenceDataEntityType.CURRENCY), eq(entityId)))
                .thenReturn(Mono.just(true));
        ActivateReferenceEntitySagaCommand cmd = ActivateReferenceEntitySagaCommand.builder()
                .entityType(ReferenceDataEntityType.CURRENCY)
                .entityId(entityId)
                .targetStatus(TargetStatus.INACTIVE)
                .build();
        ExecutionContext ctx = ExecutionContext.forSaga("corr", SAGA_ACTIVATE_REFERENCE_ENTITY);

        StepVerifier.create(saga.validateCrossEntity(cmd, ctx))
                .expectNext(SENTINEL_CROSS_CHECK_PASSED)
                .verifyComplete();
    }

    @Test
    void validateCrossEntity_fails_whenActiveReferencesExist() {
        UUID entityId = UUID.randomUUID();
        when(validator.hasCrossCheck(ReferenceDataEntityType.CURRENCY)).thenReturn(true);
        when(validator.canDeactivate(eq(ReferenceDataEntityType.CURRENCY), eq(entityId)))
                .thenReturn(Mono.just(false));
        ActivateReferenceEntitySagaCommand cmd = ActivateReferenceEntitySagaCommand.builder()
                .entityType(ReferenceDataEntityType.CURRENCY)
                .entityId(entityId)
                .targetStatus(TargetStatus.INACTIVE)
                .build();
        ExecutionContext ctx = ExecutionContext.forSaga("corr", SAGA_ACTIVATE_REFERENCE_ENTITY);

        StepVerifier.create(saga.validateCrossEntity(cmd, ctx))
                .expectError(ReferenceDataConflictException.class)
                .verify();
    }

    @Test
    void validateCrossEntity_failsFast_onUnsupportedEntityType() {
        when(registry.contains(any(ReferenceDataEntityType.class))).thenReturn(false);
        ActivateReferenceEntitySagaCommand cmd = ActivateReferenceEntitySagaCommand.builder()
                .entityType(ReferenceDataEntityType.CURRENCY)
                .entityId(UUID.randomUUID())
                .targetStatus(TargetStatus.ACTIVE)
                .build();

        StepVerifier.create(saga.validateCrossEntity(cmd, ExecutionContext.forSaga("corr", SAGA_ACTIVATE_REFERENCE_ENTITY)))
                .expectError(ReferenceDataValidationException.class)
                .verify();
    }

    @Test
    void captureSnapshot_dispatchesCommandAndStoresInCtx() {
        ExecutionContext ctx = ExecutionContext.forSaga("corr", SAGA_ACTIVATE_REFERENCE_ENTITY);
        UUID entityId = UUID.randomUUID();
        ctx.putVariable(CTX_ENTITY_TYPE, ReferenceDataEntityType.CURRENCY);
        ctx.putVariable(CTX_PRIMARY_ENTITY_ID, entityId);

        Object snapshot = new Object();
        AtomicReference<Command<?>> dispatched = new AtomicReference<>();
        when(commandBus.send(any(Command.class))).thenAnswer(inv -> {
            dispatched.set(inv.getArgument(0));
            return Mono.just(snapshot);
        });

        StepVerifier.create(saga.captureSnapshot(ctx))
                .expectNext(snapshot)
                .verifyComplete();

        assertThat(dispatched.get()).isInstanceOf(CaptureEntitySnapshotSagaCommand.class);
        assertThat(ctx.getVariableAs(CTX_PREVIOUS_STATE, Object.class)).isSameAs(snapshot);
    }

    @Test
    void applyStatusChange_dispatchesCommandWithCtxState() {
        ExecutionContext ctx = ExecutionContext.forSaga("corr", SAGA_ACTIVATE_REFERENCE_ENTITY);
        UUID entityId = UUID.randomUUID();
        ctx.putVariable(CTX_ENTITY_TYPE, ReferenceDataEntityType.CURRENCY);
        ctx.putVariable(CTX_PRIMARY_ENTITY_ID, entityId);
        ctx.putVariable("targetStatus", TargetStatus.ACTIVE);

        AtomicReference<Command<?>> dispatched = new AtomicReference<>();
        when(commandBus.send(any(Command.class))).thenAnswer(inv -> {
            dispatched.set(inv.getArgument(0));
            return Mono.just(entityId);
        });

        StepVerifier.create(saga.applyStatusChange(ctx))
                .expectNext(entityId)
                .verifyComplete();

        assertThat(dispatched.get()).isInstanceOf(ActivateReferenceEntitySagaCommand.class);
        ActivateReferenceEntitySagaCommand sent = (ActivateReferenceEntitySagaCommand) dispatched.get();
        assertThat(sent.getEntityType()).isEqualTo(ReferenceDataEntityType.CURRENCY);
        assertThat(sent.getEntityId()).isEqualTo(entityId);
        assertThat(sent.getTargetStatus()).isEqualTo(TargetStatus.ACTIVE);
    }

    @Test
    void revertStatusChange_dispatchesReapplyCommand() {
        ExecutionContext ctx = ExecutionContext.forSaga("corr", SAGA_ACTIVATE_REFERENCE_ENTITY);
        UUID entityId = UUID.randomUUID();
        Object snapshot = new Object();
        ctx.putVariable(CTX_ENTITY_TYPE, ReferenceDataEntityType.CURRENCY);
        ctx.putVariable(CTX_PRIMARY_ENTITY_ID, entityId);
        ctx.putVariable(CTX_PREVIOUS_STATE, snapshot);

        AtomicReference<Command<?>> dispatched = new AtomicReference<>();
        when(commandBus.send(any(Command.class))).thenAnswer(inv -> {
            dispatched.set(inv.getArgument(0));
            return Mono.empty();
        });

        StepVerifier.create(saga.revertStatusChange(ctx)).verifyComplete();
        assertThat(dispatched.get()).isInstanceOf(ReapplyPreviousStateSagaCommand.class);
        ReapplyPreviousStateSagaCommand sent = (ReapplyPreviousStateSagaCommand) dispatched.get();
        assertThat(sent.getPreviousState()).isSameAs(snapshot);
    }
}
