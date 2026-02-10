package com.firefly.domain.configuration.infra.clients;

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
