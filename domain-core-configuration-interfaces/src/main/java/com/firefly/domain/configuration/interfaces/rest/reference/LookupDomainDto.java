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
public class LookupDomainDto {

    private UUID domainId;

    @NotBlank
    private String domainCode;

    @NotBlank
    private String domainName;

    private String domainDesc;
    private UUID parentDomainId;
    private Boolean multiselectAllowed;
    private Boolean hierarchyAllowed;
    private Boolean tenantOverridable;
    private String extraJson;
    private UUID tenantId;
}
