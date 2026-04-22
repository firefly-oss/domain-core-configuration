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
public class AdministrativeDivisionDto {

    private UUID divisionId;

    @NotNull
    private UUID countryId;

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String level;
    private UUID parentDivisionId;
    private String postalCodePattern;
    private String timeZone;
}
