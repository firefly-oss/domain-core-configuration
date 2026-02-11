package com.firefly.domain.configuration.infra.services;

import org.fireflyframework.data.enrichment.EnricherMetadata;
import org.fireflyframework.data.event.EnrichmentEventPublisher;
import org.fireflyframework.data.model.EnrichmentRequest;
import org.fireflyframework.data.observability.JobMetricsService;
import org.fireflyframework.data.observability.JobTracingService;
import org.fireflyframework.data.resiliency.ResiliencyDecoratorService;
import org.fireflyframework.data.service.DataEnricher;
import com.firefly.domain.configuration.infra.dtos.orbis.OrbisDataResponse;
import com.firefly.domain.configuration.interfaces.rest.SelectCompanyResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
@EnricherMetadata(
    providerName = "Orbis BvD",
    tenantId = "efdd318a-d07a-45a6-bfbf-a9922f696c6f",
    type = "company-details",
    priority = 100
)
public class OrbisEnricher extends DataEnricher<SelectCompanyResponse, OrbisDataResponse, SelectCompanyResponse> {

    private final OrbisService orbisService;

    public OrbisEnricher(
            JobTracingService tracingService,
            JobMetricsService metricsService,
            ResiliencyDecoratorService resiliencyService,
            EnrichmentEventPublisher eventPublisher,
            OrbisService orbisService) {
        super(tracingService, metricsService, resiliencyService, eventPublisher, SelectCompanyResponse.class);
        this.orbisService = orbisService;
    }

    @Override
    protected Mono<OrbisDataResponse> fetchProviderData(EnrichmentRequest request) {
        String bvdId = (String) request.requireParam("bvdId");
        String domain = (String) request.getParameters().get("domain");
        return orbisService.getCompanyData(bvdId, domain);
    }

    @Override
    protected SelectCompanyResponse mapToTarget(OrbisDataResponse response) {
        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            return null;
        }

        var data = response.getData().get(0);
        return SelectCompanyResponse.builder()
                .name(data.getName())
                .bvdId(data.getBvdIdNumber())
                .city(data.getCity())
                .country(data.getCountry())
                .address(data.getAddressLine1())
                .postcode(data.getPostcode())
                .nationalId(data.getNationalId())
                .nationalIdLabel(data.getNationalIdLabel())
                .isin(data.getIsin())
                .phoneOrFax(data.getPhoneNumber())
                .emailOrWebsite(combineLists(data.getEmail(), data.getWebsite()))
                .build();
    }

    private List<String> combineLists(List<String> list1, List<String> list2) {
        List<String> result = new ArrayList<>();
        if (list1 != null) result.addAll(list1);
        if (list2 != null) result.addAll(list2);
        return result.isEmpty() ? null : result;
    }
}
