package com.firefly.domain.configuration.core.config.reference.rule;

import com.firefly.common.reference.master.data.sdk.api.RuleOperationTypeApi;
import com.firefly.common.reference.master.data.sdk.model.FilterRequestRuleOperationTypeDTO;
import com.firefly.common.reference.master.data.sdk.model.PaginationRequest;
import com.firefly.common.reference.master.data.sdk.model.RuleOperationTypeDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import com.firefly.domain.configuration.interfaces.rest.reference.RuleOperationTypeDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

@Service
public class RuleOperationTypeConfigService
        extends AbstractReferenceDataService<RuleOperationTypeApi, RuleOperationTypeDTO, RuleOperationTypeDto, UUID> {

    public RuleOperationTypeConfigService(RuleOperationTypeApi api, RuleOperationTypeMapper mapper) {
        super(api, mapper);
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.RULE_OPERATION_TYPE; }
    @Override protected Mono<RuleOperationTypeDTO> sdkCreate(RuleOperationTypeDTO p, String k) { return api.createRuleOperationType(p, k); }
    @Override protected Mono<RuleOperationTypeDTO> sdkUpdate(UUID id, RuleOperationTypeDTO p, String k) { return api.updateRuleOperationType(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteRuleOperationType(id, k); }
    @Override protected Mono<RuleOperationTypeDTO> sdkGetById(UUID id) { return api.getRuleOperationType(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<RuleOperationTypeDTO> sdkList(ReferenceDataFilter filter) {
        FilterRequestRuleOperationTypeDTO request = new FilterRequestRuleOperationTypeDTO();
        PaginationRequest pagination = new PaginationRequest();
        pagination.setPageNumber(filter.getPage() != null ? filter.getPage() : 0);
        pagination.setPageSize(filter.getSize() != null ? filter.getSize() : 100);
        request.setPagination(pagination);
        return api.filterRuleOperationTypes(request, UUID.randomUUID().toString())
                .flatMapIterable(resp -> resp.getContent() != null ? resp.getContent() : Collections.emptyList());
    }

    @Override protected UUID extractId(RuleOperationTypeDTO r) { return r.getOperationTypeId(); }
}
