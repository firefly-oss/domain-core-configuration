package com.firefly.domain.configuration.web.controller;

import com.firefly.domain.configuration.core.config.services.ConfigurationService;
import com.firefly.domain.configuration.interfaces.rest.InitConfigResponse;
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
