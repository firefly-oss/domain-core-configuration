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
public class ContractDocumentTypeDto {

    private UUID documentTypeId;

    @NotBlank
    private String documentCode;

    @NotBlank
    private String name;

    private String description;
    private Boolean isActive;
}
