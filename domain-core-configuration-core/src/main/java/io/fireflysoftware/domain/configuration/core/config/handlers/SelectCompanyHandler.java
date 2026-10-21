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
import io.fireflysoftware.domain.configuration.core.config.queries.SelectCompanyQuery;
import io.fireflysoftware.domain.configuration.infra.services.OrbisService;
import io.fireflysoftware.domain.configuration.interfaces.rest.SelectCompanyRequest;
import io.fireflysoftware.domain.configuration.interfaces.rest.SelectCompanyResponse;
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