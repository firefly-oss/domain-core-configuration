package io.fireflysoftware.domain.configuration.core.config.services;

import io.fireflysoftware.domain.configuration.core.config.queries.SelectCompanyQuery;
import io.fireflysoftware.domain.configuration.interfaces.rest.SelectCompanyResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CompanyService {

    Mono<List<SelectCompanyResponse>> selectCompany(SelectCompanyQuery query);

}
