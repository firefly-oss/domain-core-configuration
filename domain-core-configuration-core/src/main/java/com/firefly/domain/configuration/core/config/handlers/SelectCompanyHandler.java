package com.firefly.domain.configuration.core.config.handlers;

import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import com.firefly.domain.configuration.core.config.queries.SelectCompanyQuery;
import com.firefly.domain.configuration.infra.services.OrbisService;
import com.firefly.domain.configuration.interfaces.rest.SelectCompanyRequest;
import com.firefly.domain.configuration.interfaces.rest.SelectCompanyResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@QueryHandlerComponent
public class SelectCompanyHandler extends QueryHandler<SelectCompanyQuery, List<SelectCompanyResponse>> {

    private final OrbisService orbisService;

    public SelectCompanyHandler(OrbisService orbisService) {
        this.orbisService = orbisService;
    }

    @Override
    protected Mono<List<SelectCompanyResponse>> doHandle(SelectCompanyQuery query) {
        SelectCompanyRequest request = new SelectCompanyRequest();
        request.setName(query.getName());
        request.setCity(query.getCity());
        request.setCountry(query.getCountry());
        request.setAddress(query.getAddress());
        request.setNationalId(query.getNationalId());
        request.setPostCode(query.getPostCode());

        return orbisService.matchCompany(request);
    }

}