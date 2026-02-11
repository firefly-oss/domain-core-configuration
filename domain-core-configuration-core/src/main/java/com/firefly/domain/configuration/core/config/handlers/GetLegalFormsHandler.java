package com.firefly.domain.configuration.core.config.handlers;

import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import com.firefly.common.reference.master.data.sdk.api.LegalFormsApi;
import com.firefly.common.reference.master.data.sdk.model.FilterRequestLegalFormDTO;
import com.firefly.common.reference.master.data.sdk.model.LegalFormDTO;
import com.firefly.common.reference.master.data.sdk.model.PaginationRequest;
import com.firefly.common.reference.master.data.sdk.model.PaginationResponseLegalFormDTO;
import com.firefly.domain.configuration.core.config.queries.LegalFormQuery;
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
