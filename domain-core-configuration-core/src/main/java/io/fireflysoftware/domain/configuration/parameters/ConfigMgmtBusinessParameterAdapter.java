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

package io.fireflysoftware.domain.configuration.parameters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.fireflysoftware.common.config.sdk.api.ConfigurationAuditsApi;
import io.fireflysoftware.common.config.sdk.api.EnvironmentConfigurationsApi;
import io.fireflysoftware.common.config.sdk.api.FeatureFlagsApi;
import io.fireflysoftware.common.config.sdk.model.ConfigurationAuditDTO;
import io.fireflysoftware.common.config.sdk.model.EnvironmentConfigDTO;
import io.fireflysoftware.common.config.sdk.model.FeatureFlagDTO;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
 * Reads and writes business parameters through core-common-config-mgmt.
 */
public final class ConfigMgmtBusinessParameterAdapter implements BusinessParameterPort {

    private final EnvironmentConfigurationsApi environments;
    private final FeatureFlagsApi flags;
    private final ConfigurationAuditsApi audits;
    private final ObjectMapper mapper;
    private final String environmentName;

    public ConfigMgmtBusinessParameterAdapter(EnvironmentConfigurationsApi environments,
                                               FeatureFlagsApi flags,
                                               ConfigurationAuditsApi audits,
                                               String environmentName) {
        this(environments, flags, audits, new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS), environmentName);
    }

    ConfigMgmtBusinessParameterAdapter(EnvironmentConfigurationsApi environments,
                                        FeatureFlagsApi flags,
                                        ConfigurationAuditsApi audits,
                                        ObjectMapper mapper,
                                        String environmentName) {
        this.environments = environments;
        this.flags = flags;
        this.audits = audits;
        this.mapper = mapper;
        this.environmentName = environmentName;
    }

    @Override
    public Mono<BusinessParameter> findByKey(String key) {
        ParameterRules.requireKey(key);
        String trimmed = key.trim();
        return environmentRows()
                .map(rows -> match(rows, "configKey", trimmed))
                .flatMap(rows -> rows.isEmpty()
                        ? flagRows().map(flags -> match(flags, "featureKey", trimmed))
                        : Mono.just(rows))
                .flatMap(rows -> rows.isEmpty() ? Mono.empty() : Mono.just(toParameter(rows.get(0))));
    }

    @Override
    public Flux<BusinessParameter> list() {
        return environmentRows()
                .flatMapMany(Flux::fromIterable)
                .map(this::toParameter)
                .concatWith(flagRows().flatMapMany(Flux::fromIterable).map(this::toParameter))
                .sort(Comparator.comparing(BusinessParameter::key));
    }

    @Override
    public Mono<BusinessParameter> findById(UUID id) {
        return readEnvironment(id)
                .switchIfEmpty(Mono.defer(() -> readFlag(id)))
                .map(this::toParameter);
    }

    @Override
    public Mono<BusinessParameter> create(BusinessParameter draft) {
        ParameterRules.requireWritable(draft);
        if (draft.kind() == ParameterKind.SWITCH) {
            return flags.createFeatureFlag(flag(featureFlagBody(draft)), key())
                    .map(saved -> toParameter(mapper.valueToTree(saved)));
        }
        return environments.createEnvironmentConfig(environment(environmentBody(draft)), key())
                .map(saved -> toParameter(mapper.valueToTree(saved)));
    }

    @Override
    public Mono<BusinessParameter> update(UUID id, BusinessParameter draft) {
        ParameterRules.requireWritable(draft);
        return requireNode(id).flatMap(current -> {
            ObjectNode body = (ObjectNode) current.deepCopy();
            if (pathFor(current).equals("feature-flag")) {
                body.put("featureKey", draft.key().trim());
                body.put("featureName", draft.key().trim());
                body.put("enabled", Boolean.parseBoolean(draft.value().trim()));
                if (draft.description() != null) {
                    body.put("description", draft.description());
                }
                return flags.updateFeatureFlag(id, flag(body), key()).map(saved -> toParameter(mapper.valueToTree(saved)));
            }
            body.put("configKey", draft.key().trim());
            body.put("configValue", draft.value().trim());
            body.put("metadata", metadata(draft.kind()));
            if (draft.description() != null) {
                body.put("description", draft.description());
            }
            return environments.updateEnvironmentConfig(id, environment(body), key())
                    .map(saved -> toParameter(mapper.valueToTree(saved)));
        });
    }

    @Override
    public Mono<BusinessParameter> changeStatus(UUID id, ParameterStatus status) {
        if (status == null) {
            return Mono.error(new IllegalArgumentException("Parameter status is required"));
        }
        return requireNode(id).flatMap(current -> {
            BusinessParameter previous = toParameter(current);
            ObjectNode body = (ObjectNode) current.deepCopy();
            body.put("active", status == ParameterStatus.ACTIVE);
            Mono<JsonNode> updated = pathFor(current).equals("feature-flag")
                    ? flags.updateFeatureFlag(id, flag(body), key()).map(saved -> (JsonNode) mapper.valueToTree(saved))
                    : environments.updateEnvironmentConfig(id, environment(body), key()).map(saved -> (JsonNode) mapper.valueToTree(saved));
            return updated.flatMap(node -> recordAudit(pathFor(current), id, statusAction(status), "status",
                    previous.status().name(), status.name()).thenReturn(toParameter(node)));
        });
    }

    @Override
    public Mono<Void> remove(UUID id) {
        return requireNode(id).flatMap(current -> {
            BusinessParameter previous = toParameter(current);
            String kind = pathFor(current);
            Mono<Void> deleted = kind.equals("feature-flag")
                    ? flags.deleteFeatureFlag(id, key())
                    : environments.deleteEnvironmentConfig(id, key());
            return deleted.then(recordAudit(kind, id, "DELETE", "value", previous.value(), null));
        });
    }

    @Override
    public Flux<ParameterHistoryEntry> history(UUID id) {
        return audits.filterConfigurationAudits(0, 500, null, null, null, null, key())
                .flatMapMany(page -> Flux.fromIterable(page.getContent() == null
                        ? List.<ConfigurationAuditDTO>of() : page.getContent()))
                .filter(audit -> id.equals(audit.getEntityId()))
                .map(audit -> toHistory(mapper.valueToTree(audit)));
    }

    private Mono<JsonNode> requireNode(UUID id) {
        return readEnvironment(id)
                .switchIfEmpty(Mono.defer(() -> readFlag(id)))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Parameter was not found")));
    }

    private Mono<List<JsonNode>> environmentRows() {
        return environments.filterEnvironmentConfigs(0, 500, null, null, null, null, key())
                .map(page -> nodes(page == null ? null : page.getContent()));
    }

    private Mono<List<JsonNode>> flagRows() {
        return flags.filterFeatureFlags(0, 500, null, null, null, null, key())
                .map(page -> nodes(page == null ? null : page.getContent()));
    }

    private List<JsonNode> nodes(List<?> content) {
        if (content == null) {
            return List.of();
        }
        List<JsonNode> rows = new ArrayList<>();
        content.forEach(item -> rows.add(mapper.valueToTree(item)));
        return rows;
    }

    private static List<JsonNode> match(List<JsonNode> rows, String field, String value) {
        return rows.stream().filter(row -> value.equals(row.path(field).asText())).toList();
    }

    private Mono<JsonNode> readEnvironment(UUID id) {
        return environments.getEnvironmentConfigById(id, key())
                .map(dto -> (JsonNode) mapper.valueToTree(dto))
                .onErrorResume(ConfigMgmtBusinessParameterAdapter::missing, error -> Mono.empty());
    }

    private Mono<JsonNode> readFlag(UUID id) {
        return flags.getFeatureFlagById(id, key())
                .map(dto -> (JsonNode) mapper.valueToTree(dto))
                .onErrorResume(ConfigMgmtBusinessParameterAdapter::missing, error -> Mono.empty());
    }

    private EnvironmentConfigDTO environment(ObjectNode body) {
        try {
            return mapper.treeToValue(body, EnvironmentConfigDTO.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Environment configuration could not be written");
        }
    }

    private FeatureFlagDTO flag(ObjectNode body) {
        try {
            return mapper.treeToValue(body, FeatureFlagDTO.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Feature flag could not be written");
        }
    }

    private Mono<Void> recordAudit(String kind, UUID id, String action, String field, String oldValue, String newValue) {
        ConfigurationAuditDTO audit = new ConfigurationAuditDTO();
        audit.setEntityType(kind.equals("feature-flag") ? "FEATURE_FLAG" : "ENVIRONMENT_CONFIG");
        audit.setEntityId(id);
        audit.setAction(action);
        audit.setFieldName(field);
        audit.setOldValue(oldValue);
        audit.setNewValue(newValue);
        audit.setSourceSystem("domain-core-configuration");
        audit.setChangeCategory("CONFIGURATION");
        audit.setChangeSeverity("LOW");
        audit.setImpactScope("GLOBAL");
        audit.setRollbackAvailable(true);
        audit.setRolledBack(false);
        return audits.createConfigurationAudit(audit, key()).then();
    }

    private ObjectNode environmentBody(BusinessParameter draft) {
        ObjectNode body = mapper.createObjectNode();
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

    private ObjectNode featureFlagBody(BusinessParameter draft) {
        ObjectNode body = mapper.createObjectNode();
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
        return node.hasNonNull("featureKey") ? "feature-flag" : "environment-config";
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
        return new ParameterHistoryEntry(
                uuid(node, "id"),
                uuid(node, "entityId"),
                node.path("action").asText(),
                text(node, "fieldName"),
                text(node, "oldValue"),
                text(node, "newValue"),
                recordedAt(node));
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

    private static String key() {
        return UUID.randomUUID().toString();
    }

    private static boolean missing(Throwable error) {
        Throwable current = error;
        while (current != null) {
            if (current instanceof WebClientResponseException http && http.getStatusCode().value() == 404) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
