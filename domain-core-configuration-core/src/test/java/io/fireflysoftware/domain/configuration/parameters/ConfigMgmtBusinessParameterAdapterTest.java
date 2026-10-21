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

package io.fireflysoftware.domain.configuration.parameters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigMgmtBusinessParameterAdapterTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void createUpdateStatusAndDeleteCallTheConfigurationMicroservice() {
        CopyOnWriteArrayList<String> calls = new CopyOnWriteArrayList<>();
        UUID id = UUID.fromString("aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa");
        WebClient client = WebClient.builder().exchangeFunction(request -> {
            String path = request.url().getPath();
            calls.add(request.method() + " " + path);
            ObjectNode body = mapper.createObjectNode();
            body.put("id", id.toString());
            if (path.contains("feature-flags")) {
                body.put("featureKey", "journey.sample.switch");
                body.put("enabled", true);
                body.put("active", !path.endsWith("/status"));
            } else if (path.contains("environment-configs")) {
                body.put("configKey", "journey.sample.limit");
                body.put("configValue", "12");
                body.put("metadata", "{\"kind\":\"LIMIT\"}");
                body.put("active", true);
                body.put("description", "sample");
            } else {
                body.put("entityId", id.toString());
                body.put("action", "DEACTIVATE");
                body.put("createdAt", "2026-01-01T00:00:00Z");
            }
            if (path.endsWith("/filter")) {
                ObjectNode page = mapper.createObjectNode();
                if (path.contains("configuration-audits")) {
                    page.putArray("content").add(body);
                } else if (path.contains("feature-flags")) {
                    page.putArray("content");
                } else {
                    page.putArray("content").add(body);
                }
                return Mono.just(jsonResponse(page.toString()));
            }
            return Mono.just(jsonResponse(body.toString()));
        }).build();

        ConfigMgmtBusinessParameterAdapter adapter = new ConfigMgmtBusinessParameterAdapter(client, mapper, "sandbox");
        BusinessParameter created = adapter.create(new BusinessParameter(
                null, "journey.sample.limit", "12", ParameterKind.LIMIT, ParameterStatus.ACTIVE, "sample")).block();
        assertThat(created.value()).isEqualTo("12");
        assertThat(calls).contains("POST /api/v1/environment-configs");

        BusinessParameter updated = adapter.update(id, new BusinessParameter(
                id, "journey.sample.limit", "13", ParameterKind.LIMIT, ParameterStatus.ACTIVE, "sample")).block();
        assertThat(updated).isNotNull();
        assertThat(calls).contains("PUT /api/v1/environment-configs/" + id);

        adapter.changeStatus(id, ParameterStatus.INACTIVE).block();
        assertThat(calls).anyMatch(call -> call.startsWith("POST /api/v1/configuration-audits"));

        adapter.remove(id).block();
        assertThat(calls).contains("DELETE /api/v1/environment-configs/" + id);

        assertThat(adapter.history(id).collectList().block()).isNotEmpty();
    }

    private static ClientResponse jsonResponse(String json) {
        return ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .build();
    }
}
