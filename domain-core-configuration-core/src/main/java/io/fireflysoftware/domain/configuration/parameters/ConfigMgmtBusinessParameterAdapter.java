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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Adapter an installer selects in place of {@link SandboxBusinessParameterAdapter}.
 * It calls core-common-config-mgmt and does not open a bank, a PSP, or an identity provider.
 */
public final class ConfigMgmtBusinessParameterAdapter implements BusinessParameterPort {

    static final String ENVIRONMENT_CONFIGS = "/api/v1/environment-configs";
    static final String FEATURE_FLAGS = "/api/v1/feature-flags";
    static final String AUDITS = "/api/v1/configuration-audits";

    private final WebClient webClient;
    private final ObjectMapper mapper;
    private final String environmentName;

    public ConfigMgmtBusinessParameterAdapter(WebClient webClient) {
        this(webClient, new ObjectMapper(), "sandbox");
    }

    ConfigMgmtBusinessParameterAdapter(WebClient webClient, ObjectMapper mapper, String environmentName) {
        this.webClient = webClient;
        this.mapper = mapper;
        this.environmentName = environmentName;
    }

    @Override
    public Mono<BusinessParameter> findByKey(String key) {
        ParameterRules.requireKey(key);
        String trimmed = key.trim();
        return filter(ENVIRONMENT_CONFIGS, "configKey", trimmed)
                .flatMap(rows -> rows.isEmpty() ? filter(FEATURE_FLAGS, "featureKey", trimmed) : Mono.just(rows))
                .flatMap(rows -> rows.isEmpty() ? Mono.empty() : Mono.just(toParameter(rows.get(0))));
    }

    @Override
    public Flux<BusinessParameter> list() {
        return filter(ENVIRONMENT_CONFIGS, null, null)
                .flatMapMany(Flux::fromIterable)
                .map(this::toParameter)
                .concatWith(filter(FEATURE_FLAGS, null, null).flatMapMany(Flux::fromIterable).map(this::toParameter))
                .sort(Comparator.comparing(BusinessParameter::key));
    }

    @Override
    public Mono<BusinessParameter> findById(UUID id) {
        return read(ENVIRONMENT_CONFIGS, id)
                .switchIfEmpty(read(FEATURE_FLAGS, id))
                .map(this::toParameter);
    }

    @Override
    public Mono<BusinessParameter> create(BusinessParameter draft) {
        ParameterRules.requireWritable(draft);
        if (draft.kind() == ParameterKind.SWITCH) {
            return post(FEATURE_FLAGS, featureFlagBody(draft, null)).map(this::toParameter);
        }
        return post(ENVIRONMENT_CONFIGS, environmentBody(draft, null)).map(this::toParameter);
    }

    @Override
    public Mono<BusinessParameter> update(UUID id, BusinessParameter draft) {
        ParameterRules.requireWritable(draft);
        return requireNode(id).flatMap(current -> {
            String path = pathFor(current);
            ObjectNode body = (ObjectNode) current.deepCopy();
            if (path.equals(FEATURE_FLAGS)) {
                body.put("featureKey", draft.key().trim());
                body.put("featureName", draft.key().trim());
                body.put("enabled", Boolean.parseBoolean(draft.value().trim()));
                if (draft.description() != null) {
                    body.put("description", draft.description());
                }
            } else {
                body.put("configKey", draft.key().trim());
                body.put("configValue", draft.value().trim());
                body.put("metadata", metadata(draft.kind()));
                if (draft.description() != null) {
                    body.put("description", draft.description());
                }
            }
            return put(path, id, body).map(this::toParameter);
        });
    }

    @Override
    public Mono<BusinessParameter> changeStatus(UUID id, ParameterStatus status) {
        if (status == null) {
            return Mono.error(new IllegalArgumentException("Parameter status is required"));
        }
        return requireNode(id).flatMap(current -> {
            BusinessParameter previous = toParameter(current);
            String path = pathFor(current);
            ObjectNode body = (ObjectNode) current.deepCopy();
            body.put("active", status == ParameterStatus.ACTIVE);
            return put(path, id, body)
                    .flatMap(updated -> recordAudit(path, id, statusAction(status), "status",
                            previous.status().name(), status.name()).thenReturn(toParameter(updated)));
        });
    }

    @Override
    public Mono<Void> remove(UUID id) {
        return requireNode(id).flatMap(current -> {
            String path = pathFor(current);
            BusinessParameter previous = toParameter(current);
            return webClient.delete()
                    .uri(path + "/{id}", id)
                    .retrieve()
                    .toBodilessEntity()
                    .then(recordAudit(path, id, "DELETE", "value", previous.value(), null));
        });
    }

    @Override
    public Flux<ParameterHistoryEntry> history(UUID id) {
        return filter(AUDITS, "entityId", id.toString())
                .flatMapMany(Flux::fromIterable)
                .map(this::toHistory);
    }

    private Mono<JsonNode> requireNode(UUID id) {
        return read(ENVIRONMENT_CONFIGS, id)
                .switchIfEmpty(read(FEATURE_FLAGS, id))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Parameter was not found")));
    }

    private Mono<List<JsonNode>> filter(String path, String field, String value) {
        ObjectNode body = mapper.createObjectNode();
        ObjectNode filters = body.putObject("filters");
        if (field != null) {
            filters.put(field, value);
        }
        ObjectNode pagination = body.putObject("pagination");
        pagination.put("pageNumber", 0);
        pagination.put("pageSize", 500);
        return webClient.post()
                .uri(path + "/filter")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::readTree)
                .map(this::content);
    }

    private List<JsonNode> content(JsonNode page) {
        JsonNode content = page.get("content");
        if (content == null || !content.isArray()) {
            return List.of();
        }
        List<JsonNode> rows = new ArrayList<>();
        content.forEach(rows::add);
        return rows;
    }

    private Mono<JsonNode> read(String path, UUID id) {
        return webClient.get()
                .uri(path + "/{id}", id)
                .exchangeToMono(response -> {
                    if (response.statusCode().value() == 404) {
                        return response.releaseBody().then(Mono.empty());
                    }
                    if (response.statusCode().isError()) {
                        return response.createException().flatMap(Mono::error);
                    }
                    return response.bodyToMono(String.class).map(this::readTree);
                });
    }

    private Mono<JsonNode> post(String path, ObjectNode body) {
        return webClient.post().uri(path).bodyValue(body).retrieve().bodyToMono(String.class).map(this::readTree);
    }

    private Mono<JsonNode> put(String path, UUID id, ObjectNode body) {
        return webClient.put().uri(path + "/{id}", id).bodyValue(body).retrieve().bodyToMono(String.class).map(this::readTree);
    }

    private JsonNode readTree(String text) {
        try {
            return mapper.readTree(text);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Configuration response was not JSON");
        }
    }

    private Mono<Void> recordAudit(
            String path, UUID id, String action, String field, String oldValue, String newValue) {
        ObjectNode audit = mapper.createObjectNode();
        audit.put("entityType", path.equals(FEATURE_FLAGS) ? "FEATURE_FLAG" : "ENVIRONMENT_CONFIG");
        audit.put("entityId", id.toString());
        audit.put("action", action);
        audit.put("fieldName", field);
        if (oldValue != null) {
            audit.put("oldValue", oldValue);
        }
        if (newValue != null) {
            audit.put("newValue", newValue);
        }
        audit.put("sourceSystem", "domain-core-configuration");
        audit.put("changeCategory", "CONFIGURATION");
        audit.put("changeSeverity", "LOW");
        audit.put("impactScope", "GLOBAL");
        audit.put("rollbackAvailable", true);
        audit.put("rolledBack", false);
        return webClient.post().uri(AUDITS).bodyValue(audit).retrieve().toBodilessEntity().then();
    }

    private ObjectNode environmentBody(BusinessParameter draft, UUID id) {
        ObjectNode body = mapper.createObjectNode();
        if (id != null) {
            body.put("id", id.toString());
        }
        body.put("environmentName", environmentName);
        body.put("configKey", draft.key().trim());
        body.put("configValue", draft.value().trim());
        body.put("configType", "STRING");
        body.put("isSecret", false);
        body.put("category", "openplatform");
        body.put("active", draft.status() != ParameterStatus.INACTIVE);
        body.put("metadata", metadata(draft.kind()));
        if (draft.description() != null) {
            body.put("description", draft.description());
        }
        return body;
    }

    private ObjectNode featureFlagBody(BusinessParameter draft, UUID id) {
        ObjectNode body = mapper.createObjectNode();
        if (id != null) {
            body.put("id", id.toString());
        }
        body.put("featureKey", draft.key().trim());
        body.put("featureName", draft.key().trim());
        body.put("enabled", Boolean.parseBoolean(draft.value().trim()));
        body.put("environment", environmentName);
        body.put("active", draft.status() != ParameterStatus.INACTIVE);
        if (draft.description() != null) {
            body.put("description", draft.description());
        }
        return body;
    }

    private String metadata(ParameterKind kind) {
        return "{\"kind\":\"" + kind.name() + "\"}";
    }

    private String pathFor(JsonNode node) {
        return node.hasNonNull("featureKey") ? FEATURE_FLAGS : ENVIRONMENT_CONFIGS;
    }

    private BusinessParameter toParameter(JsonNode node) {
        if (node.hasNonNull("featureKey")) {
            boolean enabled = node.path("enabled").asBoolean(false);
            boolean active = node.path("active").asBoolean(true);
            return new BusinessParameter(
                    uuid(node, "id"),
                    node.path("featureKey").asText(),
                    Boolean.toString(enabled),
                    ParameterKind.SWITCH,
                    active ? ParameterStatus.ACTIVE : ParameterStatus.INACTIVE,
                    text(node, "description"));
        }
        return new BusinessParameter(
                uuid(node, "id"),
                node.path("configKey").asText(),
                node.path("configValue").asText(),
                kindOf(node),
                node.path("active").asBoolean(true) ? ParameterStatus.ACTIVE : ParameterStatus.INACTIVE,
                text(node, "description"));
    }

    private ParameterKind kindOf(JsonNode node) {
        String metadata = node.path("metadata").asText("");
        for (ParameterKind kind : ParameterKind.values()) {
            if (metadata.contains(kind.name())) {
                return kind;
            }
        }
        String key = node.path("configKey").asText("");
        if (key.contains("account-id")) {
            return ParameterKind.ACCOUNT;
        }
        if (key.contains("transfer-route") || key.endsWith(".route")) {
            return ParameterKind.ROUTE;
        }
        return ParameterKind.LIMIT;
    }

    private ParameterHistoryEntry toHistory(JsonNode node) {
        Instant at = recordedAt(node);
        return new ParameterHistoryEntry(
                uuid(node, "id"),
                uuid(node, "entityId"),
                node.path("action").asText(),
                text(node, "fieldName"),
                text(node, "oldValue"),
                text(node, "newValue"),
                at);
    }

    private UUID uuid(JsonNode node, String field) {
        return UUID.fromString(node.path(field).asText());
    }

    private Instant recordedAt(JsonNode node) {
        String recorded = node.path("createdAt").asText("");
        if (recorded.isBlank()) {
            return Instant.EPOCH;
        }
        try {
            return Instant.parse(recorded);
        } catch (DateTimeParseException ex) {
            return LocalDateTime.parse(recorded).toInstant(ZoneOffset.UTC);
        }
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        return value.asText();
    }

    private String statusAction(ParameterStatus status) {
        return status == ParameterStatus.ACTIVE ? "ACTIVATE" : "DEACTIVATE";
    }
}
