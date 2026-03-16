package com.firefly.domain.configuration.web.controller;

import org.fireflyframework.cqrs.query.QueryBus;
import com.firefly.common.config.sdk.model.TenantBrandingDTO;
import com.firefly.common.reference.master.data.sdk.model.PaginationResponse;
import com.firefly.domain.configuration.core.config.queries.LanguageLocaleQuery;
import com.firefly.domain.configuration.core.config.queries.LookupDomainQuery;
import com.firefly.domain.configuration.core.config.queries.TenantBrandingQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationQueryControllerTest {

    @Mock
    private QueryBus queryBus;

    private ConfigurationQueryController controller;

    @BeforeEach
    void setUp() {
        controller = new ConfigurationQueryController(queryBus);
    }

    @Test
    void shouldReturnLanguages() {
        PaginationResponse response = new PaginationResponse();
        response.setContent(List.of());

        when(queryBus.<PaginationResponse>query(any(LanguageLocaleQuery.class)))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.getLanguages())
                .assertNext(entity -> {
                    assertEquals(HttpStatus.OK, entity.getStatusCode());
                    assertNotNull(entity.getBody());
                    assertEquals(response, entity.getBody());
                })
                .verifyComplete();

        verify(queryBus).query(any(LanguageLocaleQuery.class));
    }

    @Test
    void shouldReturnLookupDomains() {
        PaginationResponse response = new PaginationResponse();
        response.setContent(List.of());

        when(queryBus.<PaginationResponse>query(any(LookupDomainQuery.class)))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.getLookupDomains())
                .assertNext(entity -> {
                    assertEquals(HttpStatus.OK, entity.getStatusCode());
                    assertNotNull(entity.getBody());
                    assertEquals(response, entity.getBody());
                })
                .verifyComplete();

        verify(queryBus).query(any(LookupDomainQuery.class));
    }

    @Test
    void shouldReturnTenantBrandings() {
        TenantBrandingDTO branding = new TenantBrandingDTO();
        List<TenantBrandingDTO> brandings = List.of(branding);

        when(queryBus.<List<TenantBrandingDTO>>query(any(TenantBrandingQuery.class)))
                .thenReturn(Mono.just(brandings));

        StepVerifier.create(controller.getTenantBrandings())
                .assertNext(entity -> {
                    assertEquals(HttpStatus.OK, entity.getStatusCode());
                    assertNotNull(entity.getBody());
                    assertEquals(1, entity.getBody().size());
                    assertEquals(branding, entity.getBody().get(0));
                })
                .verifyComplete();

        verify(queryBus).query(any(TenantBrandingQuery.class));
    }
}
