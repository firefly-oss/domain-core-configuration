package io.fireflysoftware.domain.configuration.core.config.services.impl;

import org.fireflyframework.cqrs.query.QueryBus;
import io.fireflysoftware.domain.configuration.core.config.queries.SelectCompanyQuery;
import io.fireflysoftware.domain.configuration.core.config.services.CompanyService;
import io.fireflysoftware.domain.configuration.interfaces.rest.SelectCompanyResponse;
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
