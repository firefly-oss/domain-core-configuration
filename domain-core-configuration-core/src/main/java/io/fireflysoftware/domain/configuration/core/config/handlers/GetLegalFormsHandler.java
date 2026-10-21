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
import io.fireflysoftware.common.reference.master.data.sdk.api.LegalFormsApi;
import io.fireflysoftware.common.reference.master.data.sdk.model.FilterRequestLegalFormDTO;
import io.fireflysoftware.common.reference.master.data.sdk.model.LegalFormDTO;
import io.fireflysoftware.common.reference.master.data.sdk.model.PaginationRequest;
import io.fireflysoftware.common.reference.master.data.sdk.model.PaginationResponseLegalFormDTO;
import io.fireflysoftware.domain.configuration.core.config.queries.LegalFormQuery;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@QueryHandlerComponent
public class GetLegalFormsHandler extends QueryHandler<LegalFormQuery, List<LegalFormDTO>> {

    private final LegalFormsApi legalFormsApi;

    public GetLegalFormsHandler(LegalFormsApi legalFormsApi) {
        this.legalFormsApi = legalFormsApi;
    }

    @Override
    protected Mono<List<LegalFormDTO>> doHandle(LegalFormQuery query) {
        FilterRequestLegalFormDTO filter = new FilterRequestLegalFormDTO();
        PaginationRequest pagination = new PaginationRequest();
        pagination.setPageNumber(0);
        pagination.setPageSize(100);
        filter.setPagination(pagination);
        return legalFormsApi.listLegalForms(filter, UUID.randomUUID().toString())
                .mapNotNull(PaginationResponseLegalFormDTO::getContent);
    }
}
