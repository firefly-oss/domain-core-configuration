package com.firefly.domain.configuration.infra.services;

import com.fasterxml.jackson.core.type.TypeReference;
import org.fireflyframework.client.RestClient;
import com.firefly.domain.configuration.infra.dtos.orbis.OrbisDataRequest;
import com.firefly.domain.configuration.infra.dtos.orbis.OrbisDataResponse;
import com.firefly.domain.configuration.infra.mappers.OrbisMapper;
import com.firefly.domain.configuration.interfaces.rest.SelectCompanyRequest;
import com.firefly.domain.configuration.interfaces.rest.SelectCompanyResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OrbisService {

    private final RestClient orbisRestClient;
    private final OrbisMapper orbisMapper;

    public OrbisService(RestClient orbisRestClient, OrbisMapper orbisMapper) {
        this.orbisRestClient = orbisRestClient;
        this.orbisMapper = orbisMapper;
    }

    public Mono<List<SelectCompanyResponse>> matchCompany(SelectCompanyRequest query) {
        return orbisRestClient
                .post("/v1/orbis/companies/match", new TypeReference<List<SelectCompanyResponse>>(){})
                .withBody(orbisMapper.toOrbisMatchRequest(query))
                .execute();
    }

    public Mono<OrbisDataResponse> getCompanyData(String bvdId, String domain) {
        return orbisRestClient
                .post("/v1/orbis/companies/data", OrbisDataResponse.class)
                .withHeader("Domain", domain)
                .withBody(OrbisDataRequest.forBvDId(bvdId))
                .execute();
    }
}