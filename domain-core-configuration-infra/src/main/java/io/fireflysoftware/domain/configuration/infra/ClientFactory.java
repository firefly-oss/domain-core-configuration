/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.fireflysoftware.domain.configuration.infra;

import io.fireflysoftware.common.reference.master.data.sdk.api.*;
import io.fireflysoftware.common.reference.master.data.sdk.invoker.ApiClient;
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

    @Bean
    public LanguageLocaleApi languageLocaleApi() {
        return new LanguageLocaleApi(apiClient);
    }

    @Bean
    public LookupDomainsApi lookupDomainsApi() {
        return new LookupDomainsApi(apiClient);
    }

    @Bean
    public LookupItemsApi lookupItemsApi() {
        return new LookupItemsApi(apiClient);
    }

}
