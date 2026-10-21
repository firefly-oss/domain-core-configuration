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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Environment configuration the domain SDK calls. The domain forwards each call to
 * core-common-config-mgmt and does not keep a second copy of the value.
 */
@RestController
@RequestMapping("/api/v1/environment-configs")
public class EnvironmentConfigGatewayController {

    private final WebClient core;

    public EnvironmentConfigGatewayController(
            @Value("${firefly.configuration.parameters.core-base-url:http://localhost:8188}") String coreBaseUrl) {
        this.core = WebClient.builder().baseUrl(trim(coreBaseUrl)).build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> create(@RequestBody String body) {
        return forward(core.post()
                .uri("/api/v1/environment-configs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body));
    }

    @PostMapping(value = "/filter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> filter(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "200") int size,
            @RequestBody(required = false) String body) {
        return forward(core.post()
                .uri(uri -> uri.path("/api/v1/environment-configs/filter")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body == null || body.isBlank() ? "{}" : body));
    }

    private static Mono<ResponseEntity<String>> forward(WebClient.RequestHeadersSpec<?> request) {
        return request.exchangeToMono(response -> response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .map(payload -> ResponseEntity.status(response.statusCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(payload)));
    }

    private static String trim(String url) {
        if (url == null || url.isBlank()) {
            return "http://localhost:8188";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
