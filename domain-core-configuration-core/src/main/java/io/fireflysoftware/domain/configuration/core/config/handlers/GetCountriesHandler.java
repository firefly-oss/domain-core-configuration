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