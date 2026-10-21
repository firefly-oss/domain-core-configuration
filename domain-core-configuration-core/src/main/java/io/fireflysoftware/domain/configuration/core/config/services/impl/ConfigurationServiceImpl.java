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


package io.fireflysoftware.domain.configuration.core.config.services.impl;

import org.fireflyframework.cqrs.query.QueryBus;
import io.fireflysoftware.common.reference.master.data.sdk.model.CountryDTO;
import io.fireflysoftware.common.reference.master.data.sdk.model.CurrencyDTO;
import io.fireflysoftware.common.reference.master.data.sdk.model.LegalFormDTO;
import io.fireflysoftware.domain.configuration.core.config.queries.CountryQuery;
import io.fireflysoftware.domain.configuration.core.config.queries.CurrencyQuery;
import io.fireflysoftware.domain.configuration.interfaces.rest.InitConfigResponse;
import io.fireflysoftware.domain.configuration.core.config.queries.LegalFormQuery;
import io.fireflysoftware.domain.configuration.core.config.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    private final QueryBus queryBus;

    @Autowired
    public ConfigurationServiceImpl(QueryBus queryBus){
        this.queryBus = queryBus;
    }

    @Override
    public Mono<InitConfigResponse> getInitConfiguration() {
        Mono<List<CountryDTO>> countries = queryBus.query(CountryQuery.builder().build());
        Mono<List<CurrencyDTO>> currencies = queryBus.query(CurrencyQuery.builder().build());
        Mono<List<LegalFormDTO>> legalForms = queryBus.query(LegalFormQuery.builder().build());

        return Mono.zip(countries, currencies, legalForms)
                .map(t -> InitConfigResponse.builder()
                        .countries(t.getT1())
                        .currencies(t.getT2())
                        .legalForms(t.getT3())
                        .build());
    }
}
