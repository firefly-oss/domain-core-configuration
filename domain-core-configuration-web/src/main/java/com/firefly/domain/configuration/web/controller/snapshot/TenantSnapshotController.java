package com.firefly.domain.configuration.web.controller.snapshot;

import com.firefly.domain.configuration.core.config.services.ConfigurationService;
import com.firefly.domain.configuration.interfaces.rest.TenantSnapshotResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Returns an aggregated master-data snapshot for a tenant. Backoffice and
 * experience callers use this to bootstrap a session without issuing one request
 * per reference-data family.
 */
@RestController
@RequestMapping("/api/v1/configuration/tenants")
@Tag(name = "Tenant Snapshot", description = "Aggregated master-data bootstrap per tenant")
public class TenantSnapshotController {

    private final ConfigurationService configurationService;

    public TenantSnapshotController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Operation(summary = "Get tenant master-data snapshot",
            description = "Aggregates countries, currencies, legal forms, language locales and active lookup items for the tenant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Snapshot assembled"),
            @ApiResponse(responseCode = "400", description = "Invalid tenant identifier"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    @GetMapping("/{tenantId}/snapshot")
    public Mono<ResponseEntity<TenantSnapshotResponse>> getSnapshot(@PathVariable("tenantId") UUID tenantId) {
        return configurationService.getTenantSnapshot(tenantId).map(ResponseEntity::ok);
    }
}
