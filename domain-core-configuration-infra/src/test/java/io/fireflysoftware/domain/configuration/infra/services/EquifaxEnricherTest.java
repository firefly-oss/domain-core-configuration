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


package io.fireflysoftware.domain.configuration.infra.services;

import org.fireflyframework.data.event.EnrichmentEventPublisher;
import org.fireflyframework.data.model.EnrichmentRequest;
import org.fireflyframework.data.observability.JobMetricsService;
import org.fireflyframework.data.observability.JobTracingService;
import org.fireflyframework.data.resiliency.ResiliencyDecoratorService;
import io.fireflysoftware.domain.configuration.infra.dtos.equifax.CreditReportDTO;
import io.fireflysoftware.domain.configuration.infra.dtos.equifax.EquifaxReportRequest;
import io.fireflysoftware.domain.configuration.infra.dtos.equifax.EquifaxReportResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquifaxEnricherTest {

    @Mock
    private JobTracingService tracingService;
    @Mock
    private JobMetricsService metricsService;
    @Mock
    private ResiliencyDecoratorService resiliencyService;
    @Mock
    private EnrichmentEventPublisher eventPublisher;
    @Mock
    private EquifaxService equifaxService;
    @Mock
    private EnrichmentRequest enrichmentRequest;

    private EquifaxEnricher equifaxEnricher;

    @BeforeEach
    void setUp() {
        equifaxEnricher = new EquifaxEnricher(tracingService, metricsService, resiliencyService, eventPublisher, equifaxService);
    }

    @Test
    void shouldFetchProviderData() {
        when(enrichmentRequest.getParameters()).thenReturn(Map.of("firstName", "John", "lastName", "Doe"));
        when(equifaxService.getAccessToken()).thenReturn(Mono.just("test-token"));
        EquifaxReportResponse expectedResponse = new EquifaxReportResponse();
        when(equifaxService.getCreditReport(any(EquifaxReportRequest.class), eq("test-token"))).thenReturn(Mono.just(expectedResponse));

        EquifaxReportResponse result = equifaxEnricher.fetchProviderData(enrichmentRequest).block();

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    void shouldMapToTarget() {
        EquifaxReportResponse response = EquifaxReportResponse.builder()
                .status("completed")
                .consumers(EquifaxReportResponse.Consumers.builder()
                        .equifaxUSConsumerCreditReport(List.of(
                                EquifaxReportResponse.EquifaxUSConsumerCreditReport.builder()
                                        .reportDate("2023-10-27")
                                        .subjectName(EquifaxReportResponse.SubjectName.builder()
                                                .firstName("John")
                                                .lastName("Doe")
                                                .build())
                                        .models(List.of(
                                                EquifaxReportResponse.Models.builder()
                                                        .type("FICO")
                                                        .score(750)
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build())
                .build();

        CreditReportDTO result = equifaxEnricher.mapToTarget(response);

        assertNotNull(result);
        assertEquals("completed", result.getStatus());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("2023-10-27", result.getReportDate());
        assertNotNull(result.getScores());
        assertEquals(1, result.getScores().size());
        assertEquals("FICO", result.getScores().get(0).getModelName());
        assertEquals(750, result.getScores().get(0).getValue());
    }
}
