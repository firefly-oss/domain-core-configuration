package com.firefly.domain.configuration.core.config.reference.legal;

import com.firefly.common.reference.master.data.sdk.api.LegalFormsApi;
import com.firefly.common.reference.master.data.sdk.model.FilterRequestLegalFormDTO;
import com.firefly.common.reference.master.data.sdk.model.LegalFormDTO;
import com.firefly.common.reference.master.data.sdk.model.PaginationRequest;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.LegalFormConfigDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

@Service
public class LegalFormConfigService
        extends AbstractReferenceDataService<LegalFormsApi, LegalFormDTO, LegalFormConfigDto, UUID> {

    public LegalFormConfigService(LegalFormsApi api, LegalFormMapper mapper) {
        super(api, mapper);
    }

    @Override
    public ReferenceDataEntityType entityType() {
        return ReferenceDataEntityType.LEGAL_FORM;
    }

    @Override
    protected Mono<LegalFormDTO> sdkCreate(LegalFormDTO payload, String idempotencyKey) {
        return api.createLegalForm(payload, idempotencyKey);
    }

    @Override
    protected Mono<LegalFormDTO> sdkUpdate(UUID id, LegalFormDTO payload, String idempotencyKey) {
        return api.updateLegalForm(id, payload, idempotencyKey);
    }

    @Override
    protected Mono<Void> sdkDelete(UUID id, String idempotencyKey) {
        return api.deleteLegalForm(id, idempotencyKey);
    }

    @Override
    protected Mono<LegalFormDTO> sdkGetById(UUID id) {
        return api.getLegalForm(id, UUID.randomUUID().toString());
    }

    @Override
    protected Flux<LegalFormDTO> sdkList(ReferenceDataFilter filter) {
        FilterRequestLegalFormDTO request = new FilterRequestLegalFormDTO();
        PaginationRequest pagination = new PaginationRequest();
        pagination.setPageNumber(filter.getPage() != null ? filter.getPage() : 0);
        pagination.setPageSize(filter.getSize() != null ? filter.getSize() : 100);
        request.setPagination(pagination);
        return api.listLegalForms(request, UUID.randomUUID().toString())
                .flatMapIterable(resp -> resp.getContent() != null ? resp.getContent() : Collections.emptyList());
    }

    @Override
    protected UUID extractId(LegalFormDTO response) {
        return response.getLegalFormId();
    }
}
