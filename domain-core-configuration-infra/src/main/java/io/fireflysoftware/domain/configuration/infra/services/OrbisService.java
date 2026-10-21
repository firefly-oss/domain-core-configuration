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

import com.fasterxml.jackson.core.type.TypeReference;
import org.fireflyframework.client.RestClient;
import io.fireflysoftware.domain.configuration.infra.dtos.orbis.OrbisDataRequest;
import io.fireflysoftware.domain.configuration.infra.dtos.orbis.OrbisDataResponse;
import io.fireflysoftware.domain.configuration.infra.mappers.OrbisMapper;
import io.fireflysoftware.domain.configuration.interfaces.rest.SelectCompanyRequest;
import io.fireflysoftware.domain.configuration.interfaces.rest.SelectCompanyResponse;
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