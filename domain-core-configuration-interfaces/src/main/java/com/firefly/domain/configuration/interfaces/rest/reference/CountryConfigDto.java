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
public class CountryConfigDto {

    private UUID countryId;

    @NotBlank
    @Size(min = 2, max = 3)
    private String isoCode;

    @NotBlank
    private String countryName;

    private String svgFlag;
}
