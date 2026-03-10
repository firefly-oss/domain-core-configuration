package com.firefly.domain.configuration.infra.services;

import org.fireflyframework.data.event.EnrichmentEventPublisher;
import org.fireflyframework.data.model.EnrichmentRequest;
import org.fireflyframework.data.observability.JobMetricsService;
import org.fireflyframework.data.observability.JobTracingService;
import org.fireflyframework.data.resiliency.ResiliencyDecoratorService;
import com.firefly.domain.configuration.infra.dtos.orbis.OrbisDataResponse;
import com.firefly.domain.configuration.interfaces.rest.SelectCompanyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrbisEnricherTest {

    @Mock
    private JobTracingService tracingService;
    @Mock
    private JobMetricsService metricsService;
    @Mock
    private ResiliencyDecoratorService resiliencyService;
    @Mock
    private EnrichmentEventPublisher eventPublisher;
    @Mock
    private OrbisService orbisService;
    @Mock
    private EnrichmentRequest enrichmentRequest;

    private OrbisEnricher orbisEnricher;

    @BeforeEach
    void setUp() {
        orbisEnricher = new OrbisEnricher(tracingService, metricsService, resiliencyService, eventPublisher, orbisService);
    }

    @Test
    void shouldMapToTargetCorrectly() {
        OrbisDataResponse.OrbisCompanyData companyData = new OrbisDataResponse.OrbisCompanyData();
        companyData.setName("Company Name");
        companyData.setBvdIdNumber("BVD123");
        companyData.setAddressLine1("Main St");
        companyData.setPostcode("12345");
        companyData.setCity("City");
        companyData.setCountry("Country");
        companyData.setEmail(List.of("email@test.com"));

        OrbisDataResponse response = OrbisDataResponse.builder()
                .data(List.of(companyData))
                .build();

        SelectCompanyResponse result = orbisEnricher.mapToTarget(response);

        assertNotNull(result);
        assertEquals("Company Name", result.getName());
        assertEquals("BVD123", result.getBvdId());
        assertEquals("City", result.getCity());
        assertEquals("Country", result.getCountry());
        assertEquals("Main St", result.getAddress());
        assertEquals("12345", result.getPostcode());
        assertEquals(List.of("email@test.com"), result.getEmailOrWebsite());
    }

    @Test
    void shouldCombineEmailAndWebsiteCorrectly() {
        OrbisDataResponse.OrbisCompanyData companyData = new OrbisDataResponse.OrbisCompanyData();
        companyData.setEmail(List.of("email@test.com"));
        companyData.setWebsite(List.of("www.test.com"));

        OrbisDataResponse response = OrbisDataResponse.builder()
                .data(List.of(companyData))
                .build();

        SelectCompanyResponse result = orbisEnricher.mapToTarget(response);

        assertNotNull(result);
        assertEquals(List.of("email@test.com", "www.test.com"), result.getEmailOrWebsite());
    }

    @Test
    void shouldHandleEmptyListsInMapping() {
        OrbisDataResponse.OrbisCompanyData companyData = new OrbisDataResponse.OrbisCompanyData();
        companyData.setNationalId(List.of("NAT123"));

        OrbisDataResponse response = OrbisDataResponse.builder()
                .data(List.of(companyData))
                .build();

        SelectCompanyResponse result = orbisEnricher.mapToTarget(response);

        assertNotNull(result);
        assertEquals(List.of("NAT123"), result.getNationalId());
        assertNull(result.getEmailOrWebsite());
        assertNull(result.getPhoneOrFax());
    }

    @Test
    void shouldReturnNullWhenResponseIsEmpty() {
        OrbisDataResponse response = OrbisDataResponse.builder()
                .data(List.of())
                .build();

        SelectCompanyResponse result = orbisEnricher.mapToTarget(response);

        assertNull(result);
    }

    @Test
    void shouldFetchProviderData() {
        when(enrichmentRequest.requireParam("bvdId")).thenReturn("BVD123");
        when(enrichmentRequest.getParameters()).thenReturn(Map.of("domain", "test-domain"));
        OrbisDataResponse expectedResponse = new OrbisDataResponse();

        when(orbisService.getCompanyData("BVD123", "test-domain")).thenReturn(Mono.just(expectedResponse));

        OrbisDataResponse result = orbisEnricher.fetchProviderData(enrichmentRequest).block();

        assertEquals(expectedResponse, result);
    }
}
