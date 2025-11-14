package com.firefly.domain.configuration.core.config.services.impl;

import com.firefly.common.cqrs.query.QueryBus;
import com.firefly.common.reference.master.data.sdk.model.CountryDTO;
import com.firefly.common.reference.master.data.sdk.model.CurrencyDTO;
import com.firefly.common.reference.master.data.sdk.model.LegalFormDTO;
import com.firefly.domain.configuration.core.config.queries.CountryQuery;
import com.firefly.domain.configuration.core.config.queries.CurrencyQuery;
import com.firefly.domain.configuration.core.config.queries.InitConfigResponse;
import com.firefly.domain.configuration.core.config.queries.LegalFormQuery;
import com.firefly.domain.configuration.core.config.services.ConfigurationService;
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
