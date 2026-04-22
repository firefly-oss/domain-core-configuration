package com.firefly.domain.configuration.web.controller.snapshot;

import com.firefly.domain.configuration.core.config.services.ConfigurationService;
import com.firefly.domain.configuration.interfaces.rest.TenantSnapshotResponse;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TenantSnapshotControllerTest {

    @Test
    void getSnapshot_delegatesToServiceAndReturns200() {
        ConfigurationService service = mock(ConfigurationService.class);
        UUID tenantId = UUID.randomUUID();
        TenantSnapshotResponse expected = TenantSnapshotResponse.builder()
                .tenantId(tenantId)
                .countries(List.of())
                .currencies(List.of())
                .legalForms(List.of())
                .languageLocales(List.of())
                .lookupItems(List.of())
                .build();
        when(service.getTenantSnapshot(eq(tenantId))).thenReturn(Mono.just(expected));

        TenantSnapshotController controller = new TenantSnapshotController(service);
        StepVerifier.create(controller.getSnapshot(tenantId))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(response.getBody()).isEqualTo(expected);
                })
                .verifyComplete();
    }
}
