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
public class ActivityCodeDto {

    private UUID activityCodeId;
    private UUID countryId;

    @NotBlank
    private String code;

    private String classificationSys;
    private String description;
    private UUID parentCodeId;
    private Boolean highRisk;
    private String riskFactors;
}
