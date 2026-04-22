package com.firefly.domain.configuration.web.controller.orchestration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.core.config.services.ConfigurationService;
import com.firefly.domain.configuration.core.config.workflows.commands.ActivateReferenceEntitySagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.ActivateReferenceEntitySagaCommand.TargetStatus;
import com.firefly.domain.configuration.core.config.workflows.commands.UpdateReferenceDataSagaCommand;
import com.firefly.domain.configuration.interfaces.rest.orchestration.UpdateReferenceDataSagaRequest;
import com.firefly.domain.configuration.web.controller.reference.base.ReferenceDataTypeResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Orchestration endpoints for the cross-entity reference-data sagas introduced
 * in Step 4A.7. Accepts kebab-case entity type tokens (e.g. {@code currency},
 * {@code legal-form}) and dispatches to the matching saga via
 * {@link ConfigurationService}.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/configuration/reference-data")
@Tag(name = "Reference Data Orchestration",
        description = "Cross-entity update and activation sagas over master-data entities")
public class ReferenceDataOrchestrationController {

    private final ConfigurationService configurationService;
    private final ObjectMapper objectMapper;

    public ReferenceDataOrchestrationController(ConfigurationService configurationService,
                                                ObjectMapper objectMapper) {
        this.configurationService = configurationService;
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "Update reference-data entity with compensation",
            description = "Runs UpdateReferenceDataSaga to atomically update the entity and optional localizations.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Update completed"),
            @ApiResponse(responseCode = "400", description = "Invalid entity type or payload"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "409", description = "Conflicting state")
    })
    @PostMapping("/updates")
    public Mono<ResponseEntity<UUID>> update(@Valid @RequestBody UpdateReferenceDataSagaRequest request) {
        ReferenceDataEntityType entityType = parseEntityType(request.getEntityType());
        if (entityType == null) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        Object payload = convertPayload(entityType, request.getPayload());
        UpdateReferenceDataSagaCommand cmd = UpdateReferenceDataSagaCommand.builder()
                .entityType(entityType)
                .entityId(request.getEntityId())
                .payload(payload)
                .localizations(request.getLocalizations())
                .build();
        return configurationService.updateReferenceData(cmd).map(ResponseEntity::ok);
    }

    @Operation(summary = "Activate reference-data entity",
            description = "Runs ActivateReferenceEntitySaga with targetStatus=ACTIVE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activated"),
            @ApiResponse(responseCode = "400", description = "Invalid entity type"),
            @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    @PostMapping("/{entityType}/{entityId}/activate")
    public Mono<ResponseEntity<UUID>> activate(@PathVariable("entityType") String entityType,
                                               @PathVariable("entityId") UUID entityId) {
        return dispatchStatusChange(entityType, entityId, TargetStatus.ACTIVE);
    }

    @Operation(summary = "Deactivate reference-data entity",
            description = "Runs ActivateReferenceEntitySaga with targetStatus=INACTIVE; fails with 409 when cross-entity references block deactivation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deactivated"),
            @ApiResponse(responseCode = "400", description = "Invalid entity type"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "409", description = "Active references block deactivation")
    })
    @PostMapping("/{entityType}/{entityId}/deactivate")
    public Mono<ResponseEntity<UUID>> deactivate(@PathVariable("entityType") String entityType,
                                                 @PathVariable("entityId") UUID entityId) {
        return dispatchStatusChange(entityType, entityId, TargetStatus.INACTIVE);
    }

    private Mono<ResponseEntity<UUID>> dispatchStatusChange(String rawEntityType,
                                                            UUID entityId,
                                                            TargetStatus target) {
        ReferenceDataEntityType entityType = parseEntityType(rawEntityType);
        if (entityType == null) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        ActivateReferenceEntitySagaCommand cmd = ActivateReferenceEntitySagaCommand.builder()
                .entityType(entityType)
                .entityId(entityId)
                .targetStatus(target)
                .build();
        return configurationService.activateReferenceEntity(cmd).map(ResponseEntity::ok);
    }

    private ReferenceDataEntityType parseEntityType(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return ReferenceDataEntityType.fromKebabCase(raw);
        } catch (IllegalArgumentException ex) {
            log.warn("Unknown reference-data entity type received: {}", raw);
            return null;
        }
    }

    private Object convertPayload(ReferenceDataEntityType entityType, Object rawPayload) {
        if (rawPayload == null) {
            return null;
        }
        Class<?> dtoClass = ReferenceDataTypeResolver.dtoClassFor(entityType);
        return objectMapper.convertValue(rawPayload, dtoClass);
    }
}
