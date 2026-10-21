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
import io.fireflysoftware.common.reference.master.data.sdk.api.CurrenciesApi;
import io.fireflysoftware.common.reference.master.data.sdk.model.CurrencyDTO;
import io.fireflysoftware.common.reference.master.data.sdk.model.FilterRequestCurrencyDTO;
import io.fireflysoftware.common.reference.master.data.sdk.model.PaginationRequest;
import io.fireflysoftware.common.reference.master.data.sdk.model.PaginationResponseCurrencyDTO;
import io.fireflysoftware.domain.configuration.core.config.queries.CurrencyQuery;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@QueryHandlerComponent
public class GetCurrenciesHandler extends QueryHandler<CurrencyQuery, List<CurrencyDTO>> {

    private final CurrenciesApi currenciesApi;

    public GetCurrenciesHandler(CurrenciesApi currenciesApi) {
        this.currenciesApi = currenciesApi;
    }

    @Override
    protected Mono<List<CurrencyDTO>> doHandle(CurrencyQuery query) {
        FilterRequestCurrencyDTO filter = new FilterRequestCurrencyDTO();
        PaginationRequest pagination = new PaginationRequest();
        pagination.setPageNumber(0);
        pagination.setPageSize(100);
        filter.setPagination(pagination);
        return currenciesApi.filterCurrencies(filter, UUID.randomUUID().toString())
                .mapNotNull(PaginationResponseCurrencyDTO::getContent);
    }
}
