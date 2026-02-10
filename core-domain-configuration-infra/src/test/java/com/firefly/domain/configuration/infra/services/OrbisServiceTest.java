package com.firefly.domain.configuration.infra.services;

import com.fasterxml.jackson.core.type.TypeReference;
import org.fireflyframework.client.RestClient;
import com.firefly.domain.configuration.infra.dtos.orbis.OrbisDataRequest;
import com.firefly.domain.configuration.infra.dtos.orbis.OrbisDataResponse;
import com.firefly.domain.configuration.infra.mappers.OrbisMapper;
import com.firefly.domain.configuration.interfaces.rest.SelectCompanyRequest;
import com.firefly.domain.configuration.interfaces.rest.SelectCompanyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrbisServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient orbisRestClient;

    @Mock
    private OrbisMapper orbisMapper;

    private OrbisService orbisService;

    @BeforeEach
    void setUp() {
        orbisService = new OrbisService(orbisRestClient, orbisMapper);
    }

    @Test
    void shouldGetCompanyData() {
        String bvdId = "testBvdId";
        String domain = "testDomain";
        OrbisDataResponse expectedResponse = new OrbisDataResponse();

        when(orbisRestClient
                .post(eq("/v1/orbis/companies/data"), eq(OrbisDataResponse.class))
                .withHeader(eq("Domain"), eq(domain))
                .withBody(any(OrbisDataRequest.class))
                .execute())
                .thenReturn(Mono.just(expectedResponse));

        OrbisDataResponse result = orbisService.getCompanyData(bvdId, domain).block();

        assertEquals(expectedResponse, result);

        verify(orbisRestClient).post(eq("/v1/orbis/companies/data"), eq(OrbisDataResponse.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMatchCompany() {
        SelectCompanyRequest request = new SelectCompanyRequest();
        List<SelectCompanyResponse> expectedResponse = List.of(new SelectCompanyResponse());

        when(orbisRestClient
                .post(eq("/v1/orbis/companies/match"), any(TypeReference.class))
                .withBody(any())
                .execute())
                .thenReturn(Mono.just(expectedResponse));

        List<SelectCompanyResponse> result = orbisService.matchCompany(request).block();

        assertEquals(expectedResponse, result);
    }
}
