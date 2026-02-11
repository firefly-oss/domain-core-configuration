package com.firefly.domain.configuration.core.config.services.impl;

import org.fireflyframework.cqrs.query.QueryBus;
import com.firefly.domain.configuration.core.config.queries.SelectCompanyQuery;
import com.firefly.domain.configuration.core.config.services.CompanyService;
import com.firefly.domain.configuration.interfaces.rest.SelectCompanyResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final QueryBus queryBus;

    public CompanyServiceImpl(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    @Override
    public Mono<List<SelectCompanyResponse>> selectCompany(SelectCompanyQuery query) {
        return queryBus.query(query);
    }
}
