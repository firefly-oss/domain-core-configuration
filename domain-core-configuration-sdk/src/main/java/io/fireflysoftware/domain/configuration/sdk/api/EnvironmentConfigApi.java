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

package io.fireflysoftware.domain.configuration.sdk.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fireflysoftware.domain.configuration.sdk.invoker.ApiClient;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Reads and writes environment configuration on domain-core-configuration.
 */
public class EnvironmentConfigApi {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final WebClient webClient;
    private final String basePath;

    public EnvironmentConfigApi(ApiClient apiClient) {
        this.webClient = apiClient.getWebClient();
        this.basePath = trim(apiClient.getBasePath());
    }

    public Mono<String> getRequired(String key) {
        return webClient.post()
                .uri(basePath + "/api/v1/environment-configs/filter?page=0&size=200")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of())
                .retrieve()
                .bodyToMono(String.class)
                .defaultIfEmpty("{}")
                .flatMap(raw -> {
                    for (JsonNode item : items(tree(raw))) {
                        if (key.equals(item.path("configKey").asText())) {
                            String value = item.path("configValue").asText("");
                            if (!value.isBlank()) {
                                return Mono.just(value);
                            }
                        }
                    }
                    return Mono.error(new IllegalStateException("PROPERTY_NOT_SET"));
                });
    }

    public Mono<String> put(String key, String value, UUID employeeId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("environmentName", "sandbox");
        body.put("configKey", key);
        body.put("configValue", value);
        body.put("configType", "STRING");
        body.put("isSecret", false);
        body.put("description", "Staff terminal property set by " + employeeId);
        return webClient.post()
                .uri(basePath + "/api/v1/environment-configs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .defaultIfEmpty("{}")
                .map(raw -> tree(raw).path("configValue").asText(value));
    }

    static JsonNode tree(String raw) {
        if (raw == null || raw.isBlank()) {
            return MAPPER.createObjectNode();
        }
        try {
            return MAPPER.readTree(raw);
        } catch (Exception exception) {
            throw new IllegalStateException("DOMAIN_RESPONSE_UNREADABLE");
        }
    }

    static Iterable<JsonNode> items(JsonNode body) {
        if (body == null || body.isMissingNode() || body.isNull()) {
            return java.util.List.of();
        }
        if (body.isArray()) {
            return body;
        }
        for (String field : new String[]{"content", "items", "data", "results"}) {
            JsonNode node = body.path(field);
            if (node.isArray()) {
                return node;
            }
        }
        return java.util.List.of();
    }

    private static String trim(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("PROPERTY_NOT_SET");
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
