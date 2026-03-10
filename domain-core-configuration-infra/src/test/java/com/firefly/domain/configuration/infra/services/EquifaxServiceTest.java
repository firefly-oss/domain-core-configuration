package com.firefly.domain.configuration.infra.services;

import org.fireflyframework.client.RestClient;
import com.firefly.domain.configuration.infra.dtos.equifax.EquifaxReportRequest;
import com.firefly.domain.configuration.infra.dtos.equifax.EquifaxReportResponse;
import com.firefly.domain.configuration.infra.dtos.equifax.EquifaxTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquifaxServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient equifaxRestClient;

    private EquifaxService equifaxService;

    @BeforeEach
    void setUp() {
        equifaxService = new EquifaxService(equifaxRestClient, "user", "pass");
    }

    @Test
    void shouldGetAccessToken() {
        EquifaxTokenResponse expectedResponse = EquifaxTokenResponse.builder()
                .accessToken("test-token")
                .build();

        when(equifaxRestClient
                .post(eq("/v2/oauth/token"), eq(EquifaxTokenResponse.class))
                .withHeader(eq("Authorization"), anyString())
                .withHeader(eq("Content-Type"), eq(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .withBody(any())
                .execute())
                .thenReturn(Mono.just(expectedResponse));

        String result = equifaxService.getAccessToken().block();

        assertEquals("test-token", result);
        verify(equifaxRestClient).post(eq("/v2/oauth/token"), eq(EquifaxTokenResponse.class));
    }

    @Test
    void shouldGetCreditReport() {
        EquifaxReportRequest request = new EquifaxReportRequest();
        EquifaxReportResponse expectedResponse = new EquifaxReportResponse();
        String token = "test-token";

        when(equifaxRestClient
                .post(eq("/business/oneview/consumer-credit/v1/reports/credit-report"), eq(EquifaxReportResponse.class))
                .withHeader(eq("Authorization"), eq("Bearer " + token))
                .withHeader(eq("Content-Type"), eq(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(eq("efx-client-correlation-id"), anyString())
                .withBody(eq(request))
                .execute())
                .thenReturn(Mono.just(expectedResponse));

        EquifaxReportResponse result = equifaxService.getCreditReport(request, token).block();

        assertNotNull(result);
        verify(equifaxRestClient).post(eq("/business/oneview/consumer-credit/v1/reports/credit-report"), eq(EquifaxReportResponse.class));
    }
}
