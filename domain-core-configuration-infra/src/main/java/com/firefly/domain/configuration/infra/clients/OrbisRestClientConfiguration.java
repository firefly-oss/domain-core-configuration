package com.firefly.domain.configuration.infra.clients;

import org.fireflyframework.client.RestClient;
import org.fireflyframework.client.ServiceClient;
import org.fireflyframework.resilience.CircuitBreakerManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OrbisRestClientConfiguration {

    @Bean
    public RestClient orbisRestClient(WebClient.Builder webClientBuilder,
                                      CircuitBreakerManager circuitBreakerManager,
                                      @Value("${orbis.base-url:https://api.bvdinfo.com}") String baseUrl,
                                      @Value("${orbis.api-token}") String apiToken) {

        WebClient webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("ApiToken", apiToken)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();

        return ServiceClient.rest("orbis")
                .baseUrl(baseUrl)
                .jsonContentType()
                .webClient(webClient)
                .circuitBreakerManager(circuitBreakerManager)
                .build();
    }
}