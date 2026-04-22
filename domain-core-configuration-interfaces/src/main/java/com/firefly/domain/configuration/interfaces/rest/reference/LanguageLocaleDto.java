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
public class LanguageLocaleDto {

    private UUID localeId;

    @NotBlank
    private String languageCode;

    private String countryCode;

    @NotBlank
    private String localeCode;

    private String languageName;
    private String nativeName;
    private String regionName;
    private Boolean rtl;
    private Integer sortOrder;
}
