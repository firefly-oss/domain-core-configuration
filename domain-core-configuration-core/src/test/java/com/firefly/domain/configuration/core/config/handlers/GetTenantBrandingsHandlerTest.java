package com.firefly.domain.configuration.core.config.handlers;

import com.firefly.common.config.sdk.api.TenantBrandingsApi;
import com.firefly.common.config.sdk.model.PaginationResponseTenantBrandingDTO;
import com.firefly.common.config.sdk.model.TenantBrandingDTO;
import com.firefly.domain.configuration.core.config.queries.TenantBrandingQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTenantBrandingsHandlerTest {

    @Mock
    private TenantBrandingsApi tenantBrandingsApi;

    private GetTenantBrandingsHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetTenantBrandingsHandler(tenantBrandingsApi);
    }

    @Test
    void shouldReturnTenantBrandings() {
        TenantBrandingDTO branding = new TenantBrandingDTO();
        PaginationResponseTenantBrandingDTO response = new PaginationResponseTenantBrandingDTO();
        response.setContent(List.of(branding));

        when(tenantBrandingsApi.filterTenantBrandings(eq(0), eq(100), isNull(), isNull(), isNull(), isNull(), anyString()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(handler.doHandle(TenantBrandingQuery.builder().build()))
                .expectNext(List.of(branding))
                .verifyComplete();

        verify(tenantBrandingsApi).filterTenantBrandings(eq(0), eq(100), isNull(), isNull(), isNull(), isNull(), anyString());
    }
}
