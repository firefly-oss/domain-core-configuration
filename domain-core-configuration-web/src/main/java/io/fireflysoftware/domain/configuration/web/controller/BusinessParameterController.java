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

package io.fireflysoftware.domain.configuration.web.controller;

import io.fireflysoftware.domain.configuration.parameters.BusinessParameter;
import io.fireflysoftware.domain.configuration.parameters.BusinessParameterQuery;
import io.fireflysoftware.domain.configuration.parameters.ParameterHistoryEntry;
import io.fireflysoftware.domain.configuration.parameters.ParameterKind;
import io.fireflysoftware.domain.configuration.parameters.ParameterStatus;
import io.fireflysoftware.domain.configuration.parameters.workflows.BusinessParameterCommands;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Business parameters. Reads call the port directly. Writes run the sagas.
 */
@RestController
@RequestMapping("/api/v1/parameters")
@Tag(name = "Business parameters", description = "Journey limits, switches, account identifiers, and transfer routes")
public class BusinessParameterController {

    private final BusinessParameterQuery query;
    private final BusinessParameterCommands commands;

    public BusinessParameterController(BusinessParameterQuery query, BusinessParameterCommands commands) {
        this.query = query;
        this.commands = commands;
    }

    @Operation(summary = "List business parameters")
    @GetMapping
    public Mono<ResponseEntity<List<BusinessParameter>>> list() {
        return query.list().collectList().map(ResponseEntity::ok);
    }

    @Operation(summary = "Read one business parameter by key")
    @GetMapping("/by-key/{key:.+}")
    public Mono<ResponseEntity<BusinessParameter>> byKey(@PathVariable String key) {
        return query.byKey(key)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Read the audit history of a parameter")
    @GetMapping("/{id}/history")
    public Mono<ResponseEntity<List<ParameterHistoryEntry>>> history(@PathVariable UUID id) {
        return query.history(id).collectList().map(ResponseEntity::ok);
    }

    @Operation(summary = "Create a business parameter")
    @PostMapping
    public Mono<ResponseEntity<BusinessParameter>> create(@RequestBody WriteParameterRequest request) {
        BusinessParameter draft = new BusinessParameter(
                null, request.key(), request.value(), request.kind(), ParameterStatus.ACTIVE, request.description());
        return commands.create(draft)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                .onErrorResume(IllegalArgumentException.class, error -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "Update a business parameter and keep its status")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<BusinessParameter>> update(
            @PathVariable UUID id, @RequestBody WriteParameterRequest request) {
        BusinessParameter draft = new BusinessParameter(
                id, request.key(), request.value(), request.kind(), null, request.description());
        return commands.update(id, draft)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, error -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "Change parameter status and record history")
    @PostMapping("/{id}/status")
    public Mono<ResponseEntity<BusinessParameter>> changeStatus(
            @PathVariable UUID id, @RequestBody ChangeStatusRequest request) {
        return commands.changeStatus(id, request.status())
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, error -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "Remove a business parameter")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> remove(@PathVariable UUID id) {
        return commands.remove(id)
                .thenReturn(ResponseEntity.noContent().<Void>build())
                .onErrorResume(IllegalArgumentException.class, error -> Mono.just(ResponseEntity.badRequest().build()));
    }

    public record WriteParameterRequest(String key, String value, ParameterKind kind, String description) {
    }

    public record ChangeStatusRequest(ParameterStatus status) {
    }
}
