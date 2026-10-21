/*
 * Copyright 2026 Firefly Software Foundation
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

package io.fireflysoftware.domain.configuration.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.fireflysoftware.common.config.sdk.api.EnvironmentConfigurationsApi;
import io.fireflysoftware.common.config.sdk.invoker.ApiClient;
import io.fireflysoftware.common.config.sdk.model.EnvironmentConfigDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Environment configuration the domain SDK calls. Each call goes to core-common-config-mgmt.
 */
@RestController
@RequestMapping("/api/v1/environment-configs")
public class EnvironmentConfigGatewayController {

    private final EnvironmentConfigurationsApi core;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public EnvironmentConfigGatewayController(
            @Value("${firefly.configuration.parameters.core-base-url:http://localhost:8188}") String coreBaseUrl) {
        ApiClient client = new ApiClient();
        client.setBasePath(trim(coreBaseUrl));
        this.core = new EnvironmentConfigurationsApi(client);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> create(@RequestBody String body) {
        EnvironmentConfigDTO request;
        try {
            request = mapper.readValue(body, EnvironmentConfigDTO.class);
        } catch (Exception ex) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return core.createEnvironmentConfig(request, UUID.randomUUID().toString())
                .map(created -> json(HttpStatus.CREATED, created));
    }

    @PostMapping(value = "/filter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> filter(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "200") int size,
            @RequestBody(required = false) String body) {
        return core.filterEnvironmentConfigs(page, size, null, null, null, null, UUID.randomUUID().toString())
                .map(pageBody -> json(HttpStatus.OK, pageBody));
    }

    private ResponseEntity<String> json(HttpStatus status, Object body) {
        try {
            return ResponseEntity.status(status)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.writeValueAsString(body));
        } catch (Exception ex) {
            throw new IllegalStateException("Configuration response could not be written");
        }
    }

    private static String trim(String url) {
        if (url == null || url.isBlank()) {
            return "http://localhost:8188";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
