package com.firefly.domain.configuration.interfaces.rest.reference;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityDocumentDto {

    private UUID documentId;

    @NotBlank
    private String documentCode;

    @NotBlank
    private String documentName;

    private UUID categoryId;
    private UUID countryId;
    private String description;
    private String validationRegex;
    private String formatDescription;
}
