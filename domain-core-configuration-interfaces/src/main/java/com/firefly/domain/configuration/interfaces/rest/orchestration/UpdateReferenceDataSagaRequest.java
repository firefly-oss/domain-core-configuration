package com.firefly.domain.configuration.interfaces.rest.orchestration;

import com.firefly.domain.configuration.interfaces.rest.reference.LocalizationPayload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request body consumed by {@code POST /api/v1/configuration/reference-data/updates}.
 * The {@code entityType} is carried as a kebab-case token for URL consistency
 * (e.g. {@code currency}, {@code legal-form}) and resolved to
 * {@code ReferenceDataEntityType} in the web layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReferenceDataSagaRequest {

    @NotBlank
    private String entityType;

    @NotNull
    private UUID entityId;

    private Object payload;

    private List<LocalizationPayload> localizations;
}
