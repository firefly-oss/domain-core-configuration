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
public class IdentityDocumentCategoryDto {

    private UUID categoryId;

    @NotBlank
    private String categoryCode;

    @NotBlank
    private String categoryName;

    private String description;
}
