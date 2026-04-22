package com.firefly.domain.configuration.interfaces.rest.reference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConfigDto {

    private UUID currencyId;

    @NotBlank
    @Size(min = 3, max = 3)
    private String isoCode;

    @NotBlank
    private String currencyName;

    private String symbol;

    private Integer decimalPrecision;

    private Boolean isMajor;
}
