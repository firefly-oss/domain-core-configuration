/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.fireflysoftware.domain.configuration.web.controller;

import org.fireflyframework.cqrs.query.QueryBus;
import io.fireflysoftware.common.config.sdk.model.TenantBrandingDTO;
import io.fireflysoftware.common.reference.master.data.sdk.model.PaginationResponse;
import io.fireflysoftware.domain.configuration.core.config.queries.LanguageLocaleQuery;
import io.fireflysoftware.domain.configuration.core.config.queries.LookupDomainQuery;
import io.fireflysoftware.domain.configuration.core.config.queries.TenantBrandingQuery;
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
