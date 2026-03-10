package com.firefly.domain.configuration.core.config.services;

import com.firefly.domain.configuration.core.config.queries.SelectCompanyQuery;
import com.firefly.domain.configuration.interfaces.rest.SelectCompanyResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CompanyService {

    Mono<List<SelectCompanyResponse>> selectCompany(SelectCompanyQuery query);

}
