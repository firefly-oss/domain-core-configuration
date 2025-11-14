package com.firefly.domain.configuration.infra;

import com.firefly.common.reference.master.data.sdk.api.*;
import com.firefly.common.reference.master.data.sdk.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Default implementation of the ClientFactory interface.
 * Creates client service instances using the appropriate API clients and dependencies.
 */
@Component
public class ClientFactory {

    private final ApiClient apiClient;

    @Autowired
    public ClientFactory(
            MasterDataProperties masterDataProperties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(masterDataProperties.getBasePath());
    }

    @Bean
    public CountriesApi countriesApi() {
        return new CountriesApi(apiClient);
    }

    @Bean
    public CurrenciesApi currenciesApi() {
        return new CurrenciesApi(apiClient);
    }

    @Bean
    public LegalFormsApi legalFormsApi() {
        return new LegalFormsApi(apiClient);
    }

}
