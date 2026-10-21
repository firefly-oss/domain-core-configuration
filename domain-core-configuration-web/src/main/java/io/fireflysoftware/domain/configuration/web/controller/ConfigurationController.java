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

import io.fireflysoftware.domain.configuration.core.config.services.ConfigurationService;
import io.fireflysoftware.domain.configuration.interfaces.rest.InitConfigResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/configuration")
@RequiredArgsConstructor
@Tag(name = "Configuration", description = "CQ queries for configuration")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @Operation(summary = "Get init configuration", description = "Retrieve init configuration")
    @GetMapping("/init")
    public Mono<ResponseEntity<InitConfigResponse>> getInitConfiguration() {
        return configurationService.getInitConfiguration()
                .map(ResponseEntity::ok);
    }
}
