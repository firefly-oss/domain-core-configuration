package com.firefly.domain.configuration.interfaces.rest.reference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractRoleScopeDto {

    private UUID scopeId;

    @NotNull
    private UUID roleId;

    @NotBlank
    private String scopeCode;

    @NotBlank
    private String scopeName;

    private String description;
    private String actionType;
    private String resourceType;
    private Boolean isActive;
}
