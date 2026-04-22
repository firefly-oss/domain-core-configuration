package com.firefly.domain.configuration.core.config.reference.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.ContractRoleScopeApi;
import com.firefly.common.reference.master.data.sdk.model.ContractRoleScopeDTO;
import com.firefly.common.reference.master.data.sdk.model.FilterRequestContractRoleScopeDTO;
import com.firefly.common.reference.master.data.sdk.model.PaginationRequest;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.ContractRoleScopeDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ContractRoleScopeConfigService
        extends AbstractReferenceDataService<ContractRoleScopeApi, ContractRoleScopeDTO, ContractRoleScopeDto, UUID> {

    private final ObjectMapper objectMapper;

    public ContractRoleScopeConfigService(ContractRoleScopeApi api,
                                          ContractRoleScopeMapper mapper,
                                          ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.CONTRACT_ROLE_SCOPE; }
    @Override protected Mono<ContractRoleScopeDTO> sdkCreate(ContractRoleScopeDTO p, String k) { return api.createContractRoleScope(p, k); }
    @Override protected Mono<ContractRoleScopeDTO> sdkUpdate(UUID id, ContractRoleScopeDTO p, String k) { return api.updateContractRoleScope(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteContractRoleScope(id, k); }
    @Override protected Mono<ContractRoleScopeDTO> sdkGetById(UUID id) { return api.getContractRoleScope(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<ContractRoleScopeDTO> sdkList(ReferenceDataFilter filter) {
        FilterRequestContractRoleScopeDTO request = new FilterRequestContractRoleScopeDTO();
        PaginationRequest pagination = new PaginationRequest();
        pagination.setPageNumber(filter.getPage() != null ? filter.getPage() : 0);
        pagination.setPageSize(filter.getSize() != null ? filter.getSize() : 100);
        request.setPagination(pagination);
        return api.filterContractRoleScopes(request, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, ContractRoleScopeDTO.class));
    }

    @Override protected UUID extractId(ContractRoleScopeDTO r) { return r.getScopeId(); }
}
