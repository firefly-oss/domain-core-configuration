package com.firefly.domain.configuration.core.config.workflows.handlers;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataRegistry;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataValidationException;
import com.firefly.domain.configuration.core.config.workflows.commands.ActivateReferenceEntitySagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.CaptureEntitySnapshotSagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.ReapplyPreviousStateSagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.UpdateLocalizationSagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.UpdateReferenceDataSagaCommand;
import com.firefly.domain.configuration.interfaces.rest.reference.LocalizationPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReferenceDataSagaHandlersTest {

    private ReferenceDataRegistry registry;
    private ReferenceDataService<Object, UUID> service;

    @BeforeEach
    @SuppressWarnings({"rawtypes", "unchecked"})
    void setUp() {
        registry = mock(ReferenceDataRegistry.class);
        service = mock(ReferenceDataService.class);
        when(registry.resolve(any(ReferenceDataEntityType.class)))
                .thenReturn((ReferenceDataService) service);
    }

    @Test
    void captureEntitySnapshotHandler_returnsDtoFromService() {
        UUID id = UUID.randomUUID();
        Object snapshot = new Object();
        when(service.getById(id)).thenReturn(Mono.just(snapshot));

        CaptureEntitySnapshotHandler handler = new CaptureEntitySnapshotHandler(registry);
        StepVerifier.create(handler.handle(
                        new CaptureEntitySnapshotSagaCommand(ReferenceDataEntityType.COUNTRY, id)))
                .expectNext(snapshot)
                .verifyComplete();
    }

    @Test
    void updatePrimaryEntityHandler_failsFast_whenPayloadIsMissing() {
        UpdatePrimaryEntityHandler handler = new UpdatePrimaryEntityHandler(registry);
        UpdateReferenceDataSagaCommand cmd = UpdateReferenceDataSagaCommand.builder()
                .entityType(ReferenceDataEntityType.CURRENCY)
                .entityId(UUID.randomUUID())
                .payload(null)
                .build();

        StepVerifier.create(handler.handle(cmd))
                .expectError(ReferenceDataValidationException.class)
                .verify();
    }

    @Test
    void updatePrimaryEntityHandler_delegatesUpdateAndReturnsEntityId() {
        UUID id = UUID.randomUUID();
        Object payload = new Object();
        when(service.update(eq(id), eq(payload))).thenReturn(Mono.just(payload));

        UpdatePrimaryEntityHandler handler = new UpdatePrimaryEntityHandler(registry);
        UpdateReferenceDataSagaCommand cmd = UpdateReferenceDataSagaCommand.builder()
                .entityType(ReferenceDataEntityType.CURRENCY)
                .entityId(id)
                .payload(payload)
                .build();

        StepVerifier.create(handler.handle(cmd))
                .expectNext(id)
                .verifyComplete();
        verify(service).update(eq(id), eq(payload));
    }

    @Test
    void updatePrimaryEntityHandler_wrapsClassCastAsValidationException() {
        UUID id = UUID.randomUUID();
        Object payload = new Object();
        when(service.update(eq(id), eq(payload)))
                .thenReturn(Mono.error(new ClassCastException("expected FooDto")));

        UpdatePrimaryEntityHandler handler = new UpdatePrimaryEntityHandler(registry);
        UpdateReferenceDataSagaCommand cmd = UpdateReferenceDataSagaCommand.builder()
                .entityType(ReferenceDataEntityType.CURRENCY)
                .entityId(id)
                .payload(payload)
                .build();

        StepVerifier.create(handler.handle(cmd))
                .expectError(ReferenceDataValidationException.class)
                .verify();
    }

    @Test
    void applyStatusChangeHandler_setsStatusToActive_whenBooleanSetterPresent() {
        UUID id = UUID.randomUUID();
        FakeStatusDto fetched = new FakeStatusDto();
        fetched.setIsActive(false);
        when(service.getById(id)).thenReturn(Mono.just(fetched));
        when(service.update(eq(id), any())).thenReturn(Mono.just(fetched));

        ApplyStatusChangeHandler handler = new ApplyStatusChangeHandler(registry);
        ActivateReferenceEntitySagaCommand cmd = ActivateReferenceEntitySagaCommand.builder()
                .entityType(ReferenceDataEntityType.CURRENCY)
                .entityId(id)
                .targetStatus(ActivateReferenceEntitySagaCommand.TargetStatus.ACTIVE)
                .build();

        StepVerifier.create(handler.handle(cmd))
                .expectNext(id)
                .verifyComplete();

        assertThat(fetched.getIsActive()).isTrue();
    }

    @Test
    void applyStatusChangeHandler_setsStringStatus_whenSetStatusPresent() {
        UUID id = UUID.randomUUID();
        FakeStringStatusDto fetched = new FakeStringStatusDto();
        fetched.setStatus("ACTIVE");
        when(service.getById(id)).thenReturn(Mono.just(fetched));
        when(service.update(eq(id), any())).thenReturn(Mono.just(fetched));

        ApplyStatusChangeHandler handler = new ApplyStatusChangeHandler(registry);
        ActivateReferenceEntitySagaCommand cmd = ActivateReferenceEntitySagaCommand.builder()
                .entityType(ReferenceDataEntityType.COUNTRY)
                .entityId(id)
                .targetStatus(ActivateReferenceEntitySagaCommand.TargetStatus.INACTIVE)
                .build();

        StepVerifier.create(handler.handle(cmd))
                .expectNext(id)
                .verifyComplete();

        assertThat(fetched.getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    void reapplyPreviousStateHandler_callsUpdateOnService() {
        UUID id = UUID.randomUUID();
        Object snapshot = new Object();
        when(service.update(eq(id), eq(snapshot))).thenReturn(Mono.just(snapshot));

        ReapplyPreviousStateHandler handler = new ReapplyPreviousStateHandler(registry);
        ReapplyPreviousStateSagaCommand cmd = new ReapplyPreviousStateSagaCommand(
                ReferenceDataEntityType.CURRENCY, id, snapshot);

        StepVerifier.create(handler.handle(cmd)).verifyComplete();
        verify(service).update(eq(id), eq(snapshot));
    }

    @Test
    void reapplyPreviousStateHandler_skipsWhenSnapshotIsNull() {
        ReapplyPreviousStateHandler handler = new ReapplyPreviousStateHandler(registry);
        ReapplyPreviousStateSagaCommand cmd = new ReapplyPreviousStateSagaCommand(
                ReferenceDataEntityType.CURRENCY, UUID.randomUUID(), null);

        StepVerifier.create(handler.handle(cmd)).verifyComplete();
    }

    @Test
    void updateLocalizationHandler_returnsPayloadIdWhenPresent() {
        UUID localizationId = UUID.randomUUID();
        UpdateLocalizationHandler handler = new UpdateLocalizationHandler();
        LocalizationPayload payload = LocalizationPayload.builder()
                .localizationId(localizationId)
                .localeTag("es-ES")
                .languageLocaleId(UUID.randomUUID())
                .build();
        UpdateLocalizationSagaCommand cmd = new UpdateLocalizationSagaCommand(
                ReferenceDataEntityType.CURRENCY, UUID.randomUUID(), payload);

        StepVerifier.create(handler.handle(cmd))
                .expectNext(localizationId)
                .verifyComplete();
    }

    @Test
    void updateLocalizationHandler_generatesIdWhenPayloadHasNoId() {
        UpdateLocalizationHandler handler = new UpdateLocalizationHandler();
        LocalizationPayload payload = LocalizationPayload.builder()
                .localeTag("es-ES")
                .languageLocaleId(UUID.randomUUID())
                .build();
        UpdateLocalizationSagaCommand cmd = new UpdateLocalizationSagaCommand(
                ReferenceDataEntityType.CURRENCY, UUID.randomUUID(), payload);

        AtomicInteger emissions = new AtomicInteger();
        StepVerifier.create(handler.handle(cmd))
                .assertNext(id -> {
                    assertThat(id).isNotNull();
                    emissions.incrementAndGet();
                })
                .verifyComplete();
        assertThat(emissions.get()).isEqualTo(1);
    }

    // --- test DTOs -----------------------------------------------------------

    public static class FakeStatusDto {
        private Boolean isActive;

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }
    }

    public static class FakeStringStatusDto {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @SuppressWarnings("unused")
    private static List<Object> asList(Object... items) {
        return List.of(items);
    }
}
