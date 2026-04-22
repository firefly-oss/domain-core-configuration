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
public class ConsentDto {

    private UUID consentId;

    @NotBlank
    private String consentType;

    private String consentDescription;
    private Integer expiryPeriodDays;
    private String consentVersion;
    private String consentSource;
}
