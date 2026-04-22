package com.firefly.domain.configuration.web.controller.orchestration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.core.config.services.ConfigurationService;
import com.firefly.domain.configuration.core.config.workflows.commands.ActivateReferenceEntitySagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.ActivateReferenceEntitySagaCommand.TargetStatus;
import com.firefly.domain.configuration.core.config.workflows.commands.UpdateReferenceDataSagaCommand;
import com.firefly.domain.configuration.interfaces.rest.orchestration.UpdateReferenceDataSagaRequest;
import com.firefly.domain.configuration.interfaces.rest.reference.CurrencyConfigDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReferenceDataOrchestrationControllerTest {

    private ConfigurationService configurationService;
    private ReferenceDataOrchestrationController controller;

    @BeforeEach
    void setUp() {
        configurationService = mock(ConfigurationService.class);
        controller = new ReferenceDataOrchestrationController(configurationService, new ObjectMapper());
    }

    @Test
    void update_convertsPayloadToTypedDtoAndDispatchesSaga() {
        UUID entityId = UUID.randomUUID();
        UpdateReferenceDataSagaRequest request = UpdateReferenceDataSagaRequest.builder()
                .entityType("currency")
                .entityId(entityId)
                .payload(Map.of("isoCode", "EUR", "currencyName", "Euro"))
                .build();
        when(configurationService.updateReferenceData(any(UpdateReferenceDataSagaCommand.class)))
                .thenReturn(Mono.just(entityId));

        StepVerifier.create(controller.update(request))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(response.getBody()).isEqualTo(entityId);
                })
                .verifyComplete();

        ArgumentCaptor<UpdateReferenceDataSagaCommand> captor =
                ArgumentCaptor.forClass(UpdateReferenceDataSagaCommand.class);
        verify(configurationService).updateReferenceData(captor.capture());
        UpdateReferenceDataSagaCommand dispatched = captor.getValue();
        assertThat(dispatched.getEntityType()).isEqualTo(ReferenceDataEntityType.CURRENCY);
        assertThat(dispatched.getEntityId()).isEqualTo(entityId);
        assertThat(dispatched.getPayload()).isInstanceOf(CurrencyConfigDto.class);
        CurrencyConfigDto typedPayload = (CurrencyConfigDto) dispatched.getPayload();
        assertThat(typedPayload.getIsoCode()).isEqualTo("EUR");
        assertThat(typedPayload.getCurrencyName()).isEqualTo("Euro");
    }

    @Test
    void update_returns400_onUnknownEntityType() {
        UpdateReferenceDataSagaRequest request = UpdateReferenceDataSagaRequest.builder()
                .entityType("nonsense")
                .entityId(UUID.randomUUID())
                .build();

        StepVerifier.create(controller.update(request))
                .assertNext(response -> assertThat(response.getStatusCode().value()).isEqualTo(400))
                .verifyComplete();

        verify(configurationService, never()).updateReferenceData(any());
    }

    @Test
    void activate_dispatchesSagaWithActiveTarget() {
        UUID id = UUID.randomUUID();
        when(configurationService.activateReferenceEntity(any(ActivateReferenceEntitySagaCommand.class)))
                .thenReturn(Mono.just(id));

        StepVerifier.create(controller.activate("currency", id))
                .assertNext(response -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue())
                .verifyComplete();

        ArgumentCaptor<ActivateReferenceEntitySagaCommand> captor =
                ArgumentCaptor.forClass(ActivateReferenceEntitySagaCommand.class);
        verify(configurationService).activateReferenceEntity(captor.capture());
        assertThat(captor.getValue().getTargetStatus()).isEqualTo(TargetStatus.ACTIVE);
    }

    @Test
    void deactivate_dispatchesSagaWithInactiveTarget() {
        UUID id = UUID.randomUUID();
        when(configurationService.activateReferenceEntity(any(ActivateReferenceEntitySagaCommand.class)))
                .thenReturn(Mono.just(id));

        StepVerifier.create(controller.deactivate("currency", id))
                .assertNext(response -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue())
                .verifyComplete();

        ArgumentCaptor<ActivateReferenceEntitySagaCommand> captor =
                ArgumentCaptor.forClass(ActivateReferenceEntitySagaCommand.class);
        verify(configurationService).activateReferenceEntity(captor.capture());
        assertThat(captor.getValue().getTargetStatus()).isEqualTo(TargetStatus.INACTIVE);
    }

    @Test
    void activate_returns400_onUnknownEntityType() {
        StepVerifier.create(controller.activate("nonsense", UUID.randomUUID()))
                .assertNext(response -> assertThat(response.getStatusCode().value()).isEqualTo(400))
                .verifyComplete();
        verify(configurationService, never()).activateReferenceEntity(any());
    }
}
