package com.firefly.domain.configuration.core.config.reference.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.ContractTypeApi;
import com.firefly.common.reference.master.data.sdk.model.ContractTypeDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.ContractTypeDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ContractTypeConfigService
        extends AbstractReferenceDataService<ContractTypeApi, ContractTypeDTO, ContractTypeDto, UUID> {

    private final ObjectMapper objectMapper;

    public ContractTypeConfigService(ContractTypeApi api, ContractTypeMapper mapper, ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.CONTRACT_TYPE; }
    @Override protected Mono<ContractTypeDTO> sdkCreate(ContractTypeDTO p, String k) { return api.createContractType(p, k); }
    @Override protected Mono<ContractTypeDTO> sdkUpdate(UUID id, ContractTypeDTO p, String k) { return api.updateContractType(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteContractType(id, k); }
    @Override protected Mono<ContractTypeDTO> sdkGetById(UUID id) { return api.getContractType(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<ContractTypeDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listContractTypes(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, ContractTypeDTO.class));
    }

    @Override protected UUID extractId(ContractTypeDTO r) { return r.getContractId(); }
}
