package com.firefly.domain.configuration.core.config.rest;

import com.firefly.common.reference.master.data.sdk.model.CountryDTO;
import com.firefly.common.reference.master.data.sdk.model.CurrencyDTO;
import com.firefly.common.reference.master.data.sdk.model.LegalFormDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response object for init config operations.
 */
@Data
@Builder
public class InitConfigResponse {

    /**
     * List of countries.
     */
    private List<CountryDTO> countries;

    /**
     * List of currencies.
     */
    private List<CurrencyDTO> currencies;

    /**
     * List of legal forms.
     */
    private List<LegalFormDTO> legalForms;
}
