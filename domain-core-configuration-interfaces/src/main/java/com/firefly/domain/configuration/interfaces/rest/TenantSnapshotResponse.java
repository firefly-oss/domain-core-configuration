package com.firefly.domain.configuration.interfaces.rest;

import com.firefly.domain.configuration.interfaces.rest.reference.CountryConfigDto;
import com.firefly.domain.configuration.interfaces.rest.reference.CurrencyConfigDto;
import com.firefly.domain.configuration.interfaces.rest.reference.LanguageLocaleDto;
import com.firefly.domain.configuration.interfaces.rest.reference.LegalFormConfigDto;
import com.firefly.domain.configuration.interfaces.rest.reference.LookupItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Aggregated master-data snapshot returned by
 * {@code GET /api/v1/configuration/tenants/{tenantId}/snapshot}. Assembled from
 * the countries, currencies, legal-forms, language-locales and active lookup
 * items scoped to the requested tenant.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSnapshotResponse {

    private UUID tenantId;

    private List<CountryConfigDto> countries;

    private List<CurrencyConfigDto> currencies;

    private List<LegalFormConfigDto> legalForms;

    private List<LanguageLocaleDto> languageLocales;

    private List<LookupItemDto> lookupItems;
}
