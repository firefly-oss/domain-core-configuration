package com.firefly.domain.configuration.core.config.reference;

import com.firefly.common.reference.master.data.sdk.api.AdministrativeDivisionsApi;
import com.firefly.common.reference.master.data.sdk.api.BankInstitutionCodesApi;
import com.firefly.common.reference.master.data.sdk.api.CountriesApi;
import com.firefly.common.reference.master.data.sdk.api.CurrenciesApi;
import com.firefly.common.reference.master.data.sdk.api.LanguageLocaleApi;
import com.firefly.common.reference.master.data.sdk.api.LegalFormsApi;
import com.firefly.common.reference.master.data.sdk.api.TitleMasterApi;
import com.firefly.domain.configuration.core.config.reference.bank.BankConfigService;
import com.firefly.domain.configuration.core.config.reference.bank.BankMapper;
import com.firefly.domain.configuration.core.config.reference.currency.CurrencyConfigService;
import com.firefly.domain.configuration.core.config.reference.currency.CurrencyMapper;
import com.firefly.domain.configuration.core.config.reference.geography.AdministrativeDivisionConfigService;
import com.firefly.domain.configuration.core.config.reference.geography.AdministrativeDivisionMapper;
import com.firefly.domain.configuration.core.config.reference.geography.CountryConfigService;
import com.firefly.domain.configuration.core.config.reference.geography.CountryMapper;
import com.firefly.domain.configuration.core.config.reference.language.LanguageLocaleConfigService;
import com.firefly.domain.configuration.core.config.reference.language.LanguageLocaleMapper;
import com.firefly.domain.configuration.core.config.reference.legal.LegalFormConfigService;
import com.firefly.domain.configuration.core.config.reference.legal.LegalFormMapper;
import com.firefly.domain.configuration.core.config.reference.title.TitleConfigService;
import com.firefly.domain.configuration.core.config.reference.title.TitleMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Smoke tests for the geography/currency family — each service reports the correct
 * entity type and can be constructed with the expected dependencies. Orchestration
 * coverage lives in {@code AbstractReferenceDataServiceTest}.
 */
class GeographyCurrencyFamilySmokeTest {

    @Test
    void countryService_reportsCorrectEntityType() {
        CountryConfigService svc = new CountryConfigService(mock(CountriesApi.class), mock(CountryMapper.class));
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.COUNTRY);
    }

    @Test
    void currencyService_reportsCorrectEntityType() {
        CurrencyConfigService svc = new CurrencyConfigService(mock(CurrenciesApi.class), mock(CurrencyMapper.class));
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.CURRENCY);
    }

    @Test
    void bankService_reportsCorrectEntityType() {
        BankConfigService svc = new BankConfigService(
                mock(BankInstitutionCodesApi.class), mock(BankMapper.class), new com.fasterxml.jackson.databind.ObjectMapper());
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.BANK);
    }

    @Test
    void legalFormService_reportsCorrectEntityType() {
        LegalFormConfigService svc = new LegalFormConfigService(mock(LegalFormsApi.class), mock(LegalFormMapper.class));
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.LEGAL_FORM);
    }

    @Test
    void titleService_reportsCorrectEntityType() {
        TitleConfigService svc = new TitleConfigService(
                mock(TitleMasterApi.class), mock(TitleMapper.class), new com.fasterxml.jackson.databind.ObjectMapper());
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.TITLE);
    }

    @Test
    void languageLocaleService_reportsCorrectEntityType() {
        LanguageLocaleConfigService svc = new LanguageLocaleConfigService(
                mock(LanguageLocaleApi.class), mock(LanguageLocaleMapper.class), new com.fasterxml.jackson.databind.ObjectMapper());
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.LANGUAGE_LOCALE);
    }

    @Test
    void administrativeDivisionService_reportsCorrectEntityType() {
        AdministrativeDivisionConfigService svc = new AdministrativeDivisionConfigService(
                mock(AdministrativeDivisionsApi.class), mock(AdministrativeDivisionMapper.class),
                new com.fasterxml.jackson.databind.ObjectMapper());
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.ADMINISTRATIVE_DIVISION);
    }
}
