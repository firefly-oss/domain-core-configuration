package io.fireflysoftware.domain.configuration.core.config.handlers;

import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import io.fireflysoftware.common.reference.master.data.sdk.api.CountriesApi;
import io.fireflysoftware.common.reference.master.data.sdk.model.CountryDTO;
import io.fireflysoftware.common.reference.master.data.sdk.model.FilterRequestCountryDTO;
import io.fireflysoftware.common.reference.master.data.sdk.model.PaginationRequest;
import io.fireflysoftware.common.reference.master.data.sdk.model.PaginationResponseCountryDTO;
import io.fireflysoftware.domain.configuration.core.config.queries.CountryQuery;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@QueryHandlerComponent
public class GetCountriesHandler extends QueryHandler<CountryQuery, List<CountryDTO>> {

    private final CountriesApi countriesApi;

    public GetCountriesHandler(CountriesApi countriesApi) {
        this.countriesApi = countriesApi;
    }

    @Override
    protected Mono<List<CountryDTO>> doHandle(CountryQuery query) {
        FilterRequestCountryDTO filter = new FilterRequestCountryDTO();
        PaginationRequest pagination = new PaginationRequest();
        pagination.setPageNumber(0);
        pagination.setPageSize(100);
        filter.setPagination(pagination);
        return countriesApi.filterCountries(filter, UUID.randomUUID().toString())
                .mapNotNull(PaginationResponseCountryDTO::getContent);
    }

}