package io.fireflysoftware.domain.configuration.interfaces.rest;

import io.fireflysoftware.common.reference.master.data.sdk.model.CountryDTO;
import io.fireflysoftware.common.reference.master.data.sdk.model.CurrencyDTO;
import io.fireflysoftware.common.reference.master.data.sdk.model.LegalFormDTO;
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
