package com.firefly.domain.configuration.interfaces.rest.reference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Carries a single locale translation for a reference-data entity during the
 * {@code UpdateReferenceDataSaga} execution. Kept intentionally generic — the
 * concrete field set depends on the parent entity type.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalizationPayload {

    private UUID localizationId;

    @NotNull
    private UUID languageLocaleId;

    @NotBlank
    private String localeTag;

    private Map<String, String> translatedFields;
}
