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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default adapter. It returns the sandbox dataset and never opens a network connection.
 * An installer replaces this class with {@link ConfigMgmtBusinessParameterAdapter}.
 */
public final class SandboxBusinessParameterAdapter implements BusinessParameterPort {

    private final Map<UUID, BusinessParameter> byId = new ConcurrentHashMap<>();
    private final Map<String, UUID> byKey = new ConcurrentHashMap<>();
    private final Map<UUID, List<ParameterHistoryEntry>> history = new ConcurrentHashMap<>();

    public SandboxBusinessParameterAdapter() {
        SandboxParameterKey.seed().forEach(this::insert);
    }

    @Override
    public Mono<BusinessParameter> findByKey(String key) {
        return Mono.fromCallable(() -> {
            ParameterRules.requireKey(key);
            UUID id = byKey.get(key.trim());
            return id == null ? null : byId.get(id);
        }).flatMap(Mono::justOrEmpty);
    }

    @Override
    public Flux<BusinessParameter> list() {
        return Flux.defer(() -> Flux.fromIterable(byId.values().stream()
                .sorted(Comparator.comparing(BusinessParameter::key))
                .toList()));
    }

    @Override
    public Mono<BusinessParameter> findById(UUID id) {
        return Mono.justOrEmpty(byId.get(id));
    }

    @Override
    public Mono<BusinessParameter> create(BusinessParameter draft) {
        return Mono.fromCallable(() -> {
            synchronized (this) {
                ParameterRules.requireWritable(draft);
                String key = draft.key().trim();
                if (byKey.containsKey(key)) {
                    throw new IllegalArgumentException("Parameter key already exists");
                }
                UUID id = draft.id() == null ? UUID.randomUUID() : draft.id();
                if (byId.containsKey(id)) {
                    throw new IllegalArgumentException("Parameter id already exists");
                }
                BusinessParameter created = new BusinessParameter(
                        id,
                        key,
                        draft.value().trim(),
                        draft.kind(),
                        draft.status() == null ? ParameterStatus.ACTIVE : draft.status(),
                        draft.description());
                insert(created);
                appendHistory(created.id(), "CREATE", "value", null, created.value());
                return created;
            }
        });
    }

    @Override
    public Mono<BusinessParameter> update(UUID id, BusinessParameter draft) {
        return Mono.fromCallable(() -> {
            synchronized (this) {
                BusinessParameter current = required(id);
                ParameterRules.requireWritable(draft);
                String key = draft.key().trim();
                UUID owner = byKey.get(key);
                if (owner != null && !owner.equals(id)) {
                    throw new IllegalArgumentException("Parameter key already exists");
                }
                byKey.remove(current.key());
                BusinessParameter updated = new BusinessParameter(
                        id,
                        key,
                        draft.value().trim(),
                        draft.kind(),
                        current.status(),
                        draft.description());
                insert(updated);
                appendHistory(id, "UPDATE", "value", current.value(), updated.value());
                return updated;
            }
        });
    }

    @Override
    public Mono<BusinessParameter> changeStatus(UUID id, ParameterStatus status) {
        return Mono.fromCallable(() -> {
            synchronized (this) {
                if (status == null) {
                    throw new IllegalArgumentException("Parameter status is required");
                }
                BusinessParameter current = required(id);
                BusinessParameter updated = new BusinessParameter(
                        current.id(),
                        current.key(),
                        current.value(),
                        current.kind(),
                        status,
                        current.description());
                byId.put(id, updated);
                String action = status == ParameterStatus.ACTIVE ? "ACTIVATE" : "DEACTIVATE";
                appendHistory(id, action, "status", current.status().name(), status.name());
                return updated;
            }
        });
    }

    @Override
    public Mono<Void> remove(UUID id) {
        return Mono.<Void>fromRunnable(() -> {
            synchronized (this) {
                BusinessParameter current = required(id);
                byId.remove(id);
                byKey.remove(current.key());
                appendHistory(id, "DELETE", "value", current.value(), null);
            }
        });
    }

    @Override
    public Flux<ParameterHistoryEntry> history(UUID id) {
        return Flux.defer(() -> Flux.fromIterable(List.copyOf(history.getOrDefault(id, List.of()))));
    }

    private void insert(BusinessParameter parameter) {
        byId.put(parameter.id(), parameter);
        byKey.put(parameter.key(), parameter.id());
    }

    private BusinessParameter required(UUID id) {
        BusinessParameter current = byId.get(id);
        if (current == null) {
            throw new IllegalArgumentException("Parameter was not found");
        }
        return current;
    }

    private void appendHistory(UUID parameterId, String action, String field, String oldValue, String newValue) {
        history.computeIfAbsent(parameterId, ignored -> new ArrayList<>())
                .add(new ParameterHistoryEntry(
                        UUID.randomUUID(),
                        parameterId,
                        action,
                        field,
                        oldValue,
                        newValue,
                        Instant.now()));
    }
}
