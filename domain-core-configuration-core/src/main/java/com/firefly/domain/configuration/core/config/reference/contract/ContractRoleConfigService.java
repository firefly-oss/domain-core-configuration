package com.firefly.domain.configuration.core.config.reference.contract;

import com.firefly.common.reference.master.data.sdk.api.ContractRoleApi;
import com.firefly.common.reference.master.data.sdk.model.ContractRoleDTO;
import com.firefly.common.reference.master.data.sdk.model.FilterRequestContractRoleDTO;
import com.firefly.common.reference.master.data.sdk.model.PaginationRequest;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.ContractRoleDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

@Service
public class ContractRoleConfigService
        extends AbstractReferenceDataService<ContractRoleApi, ContractRoleDTO, ContractRoleDto, UUID> {

    public ContractRoleConfigService(ContractRoleApi api, ContractRoleMapper mapper) {
        super(api, mapper);
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.CONTRACT_ROLE; }
    @Override protected Mono<ContractRoleDTO> sdkCreate(ContractRoleDTO p, String k) { return api.createContractRole(p, k); }
    @Override protected Mono<ContractRoleDTO> sdkUpdate(UUID id, ContractRoleDTO p, String k) { return api.updateContractRole(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteContractRole(id, k); }
    @Override protected Mono<ContractRoleDTO> sdkGetById(UUID id) { return api.getContractRole(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<ContractRoleDTO> sdkList(ReferenceDataFilter filter) {
        FilterRequestContractRoleDTO request = new FilterRequestContractRoleDTO();
        PaginationRequest pagination = new PaginationRequest();
        pagination.setPageNumber(filter.getPage() != null ? filter.getPage() : 0);
        pagination.setPageSize(filter.getSize() != null ? filter.getSize() : 100);
        request.setPagination(pagination);
        return api.filterContractRoles(request, UUID.randomUUID().toString())
                .flatMapIterable(resp -> resp.getContent() != null ? resp.getContent() : Collections.emptyList());
    }

    @Override protected UUID extractId(ContractRoleDTO r) { return r.getRoleId(); }
}
