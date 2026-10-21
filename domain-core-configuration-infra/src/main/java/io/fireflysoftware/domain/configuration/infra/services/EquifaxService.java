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

import org.fireflyframework.client.RestClient;
import io.fireflysoftware.domain.configuration.infra.dtos.equifax.EquifaxReportRequest;
import io.fireflysoftware.domain.configuration.infra.dtos.equifax.EquifaxReportResponse;
import io.fireflysoftware.domain.configuration.infra.dtos.equifax.EquifaxTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.UUID;

@Service
public class EquifaxService {

    private final RestClient equifaxRestClient;
    private final String username;
    private final String password;

    public EquifaxService(RestClient equifaxRestClient,
                          @Value("${equifax.username:}") String username,
                          @Value("${equifax.password:}") String password) {
        this.equifaxRestClient = equifaxRestClient;
        this.username = username;
        this.password = password;
    }

    public Mono<String> getAccessToken() {
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("scope", "https://api.equifax.com/business/oneview/consumer-credit/v1");

        return equifaxRestClient
                .post("/v2/oauth/token", EquifaxTokenResponse.class)
                .withHeader("Authorization", authHeader)
                .withHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .withBody(formData)
                .execute()
                .map(EquifaxTokenResponse::getAccessToken);
    }

    public Mono<EquifaxReportResponse> getCreditReport(EquifaxReportRequest request, String token) {
        return equifaxRestClient
                .post("/business/oneview/consumer-credit/v1/reports/credit-report", EquifaxReportResponse.class)
                .withHeader("Authorization", "Bearer " + token)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withHeader("efx-client-correlation-id", UUID.randomUUID().toString())
                .withBody(request)
                .execute();
    }
}
