package com.firefly.domain.configuration.infra.services;

import org.fireflyframework.data.enrichment.EnricherMetadata;
import org.fireflyframework.data.event.EnrichmentEventPublisher;
import org.fireflyframework.data.model.EnrichmentRequest;
import org.fireflyframework.data.observability.JobMetricsService;
import org.fireflyframework.data.observability.JobTracingService;
import org.fireflyframework.data.resiliency.ResiliencyDecoratorService;
import org.fireflyframework.data.service.DataEnricher;
import com.firefly.domain.configuration.infra.dtos.equifax.CreditReportDTO;
import com.firefly.domain.configuration.infra.dtos.equifax.EquifaxReportRequest;
import com.firefly.domain.configuration.infra.dtos.equifax.EquifaxReportResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@EnricherMetadata(
        providerName = "EquifaxUS",
        tenantId = "d85dea9e-6634-4870-888f-d7cfa0e59fd2",
        type = "consumer-credit-report",
        priority = 100
)
public class EquifaxEnricher extends DataEnricher<CreditReportDTO, EquifaxReportResponse, CreditReportDTO> {

    private final EquifaxService equifaxService;

    public EquifaxEnricher(JobTracingService tracingService,
                           JobMetricsService metricsService,
                           ResiliencyDecoratorService resiliencyService,
                           EnrichmentEventPublisher eventPublisher,
                           EquifaxService equifaxService) {
        super(tracingService, metricsService, resiliencyService, eventPublisher, CreditReportDTO.class);
        this.equifaxService = equifaxService;
    }

    @Override
    protected Mono<EquifaxReportResponse> fetchProviderData(EnrichmentRequest request) {
        EquifaxReportRequest reportRequest = buildEquifaxRequest(request);
        return equifaxService.getAccessToken()
                .flatMap(token -> equifaxService.getCreditReport(reportRequest, token));
    }

    @Override
    protected CreditReportDTO mapToTarget(EquifaxReportResponse response) {
        if (response == null) {
            return null;
        }

        CreditReportDTO.CreditReportDTOBuilder builder = CreditReportDTO.builder()
                .status(response.getStatus());

        Optional.ofNullable(response.getConsumers())
                .map(EquifaxReportResponse.Consumers::getEquifaxUSConsumerCreditReport)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .ifPresent(report -> {
                    builder.reportDate(report.getReportDate());
                    builder.birthDate(report.getBirthDate());
                    builder.socialNumber(report.getSubjectSocialNum());

                    Optional.ofNullable(report.getSubjectName()).ifPresent(name -> {
                        builder.firstName(name.getFirstName());
                        builder.lastName(name.getLastName());
                    });

                    if (report.getModels() != null) {
                        builder.scores(report.getModels().stream()
                                .map(model -> CreditReportDTO.ScoreDTO.builder()
                                        .modelName(model.getType())
                                        .value(model.getScore())
                                        .build())
                                .collect(Collectors.toList()));
                    }

                    if (report.getOfacAlerts() != null) {
                        builder.alerts(report.getOfacAlerts().stream()
                                .map(alert -> CreditReportDTO.AlertDTO.builder()
                                        .type("OFAC")
                                        .message(alert.getLegalVerbiage())
                                        .build())
                                .collect(Collectors.toList()));
                    }
                });

        return builder.build();
    }

    private EquifaxReportRequest buildEquifaxRequest(EnrichmentRequest request) {
        return EquifaxReportRequest.builder()
                .consumers(EquifaxReportRequest.Consumers.builder()
                        .name(List.of(EquifaxReportRequest.Name.builder()
                                .identifier("current")
                                .firstName((String) request.getParameters().getOrDefault("firstName", "LJBKFJ"))
                                .lastName((String) request.getParameters().getOrDefault("lastName", "KHJGUFJM"))
                                .build()))
                        .socialNum(List.of(EquifaxReportRequest.SocialNum.builder()
                                .identifier("current")
                                .number((String) request.getParameters().getOrDefault("socialNumber", "666123456"))
                                .build()))
                        .addresses(List.of(EquifaxReportRequest.Address.builder()
                                .identifier("current")
                                .houseNumber((String) request.getParameters().getOrDefault("houseNumber", "123"))
                                .streetName((String) request.getParameters().getOrDefault("streetName", "POIBHHFJD"))
                                .streetType((String) request.getParameters().getOrDefault("streetType", "ST"))
                                .city((String) request.getParameters().getOrDefault("city", "ATLANTA"))
                                .state((String) request.getParameters().getOrDefault("state", "GA"))
                                .zip((String) request.getParameters().getOrDefault("zip", "30374"))
                                .build()))
                        .build())
                .customerReferenceidentifier("2C800002-DOR7")
                .customerConfiguration(EquifaxReportRequest.CustomerConfiguration.builder()
                        .equifaxUSConsumerCreditReport(EquifaxReportRequest.EquifaxUSConsumerCreditReport.builder()
                                .pdfComboIndicator("Y")
                                .memberNumber("999XX12345")
                                .securityCode("@U2")
                                .customerCode("IAPI")
                                .multipleReportIndicator("1")
                                .models(List.of(
                                        EquifaxReportRequest.Model.builder().identifier("02778").modelField(List.of("3", "GA")).build(),
                                        EquifaxReportRequest.Model.builder().identifier("05143").build(),
                                        EquifaxReportRequest.Model.builder().identifier("02916").build()
                                ))
                                .ecoaInquiryType("Individual")
                                .build())
                        .equifaxUSConsumerTwnRequest(EquifaxReportRequest.EquifaxUSConsumerTwnRequest.builder()
                                .userId("twnUser@1234")
                                .userPassword("pass123")
                                .permissiblePurposeCode("PPASSESS")
                                .templateName("Full VOI")
                                .build())
                        .equifaxUSConsumerDataxInquiryRequest(EquifaxReportRequest.EquifaxUSConsumerDataxInquiryRequest.builder()
                                .authentication(EquifaxReportRequest.Authentication.builder()
                                        .licensekey("licenseKey123")
                                        .password("passwd123")
                                        .build())
                                .build())
                        .build())
                .build();
    }
}
