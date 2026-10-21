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


package io.fireflysoftware.domain.configuration.infra.clients;

import org.fireflyframework.client.RestClient;
import org.fireflyframework.client.ServiceClient;
import org.fireflyframework.resilience.CircuitBreakerManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EquifaxRestClientConfiguration {

    @Bean
    public RestClient equifaxRestClient(WebClient.Builder webClientBuilder,
                                        CircuitBreakerManager circuitBreakerManager,
                                        @Value("${equifax.base-url:https://api.sandbox.equifax.com}") String baseUrl) {

        WebClient webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();

        return ServiceClient.rest("equifax")
                .baseUrl(baseUrl)
                .webClient(webClient)
                .circuitBreakerManager(circuitBreakerManager)
                .build();
    }
}
