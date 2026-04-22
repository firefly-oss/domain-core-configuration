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
public class RuleOperationTypeDto {

    private UUID operationTypeId;

    @NotBlank
    private String operationTypeCode;

    @NotBlank
    private String name;

    private String description;
    private Boolean isActive;
}
