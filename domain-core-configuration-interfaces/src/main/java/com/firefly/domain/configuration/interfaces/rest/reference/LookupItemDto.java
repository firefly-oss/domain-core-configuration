package com.firefly.domain.configuration.interfaces.rest.reference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LookupItemDto {

    private UUID itemId;

    @NotNull
    private UUID domainId;

    @NotBlank
    private String itemCode;

    @NotBlank
    private String itemLabelDefault;

    private String itemDesc;
    private UUID parentItemId;
    private Integer sortOrder;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private Boolean isCurrent;
    private String extraJson;
    private UUID tenantId;
}
