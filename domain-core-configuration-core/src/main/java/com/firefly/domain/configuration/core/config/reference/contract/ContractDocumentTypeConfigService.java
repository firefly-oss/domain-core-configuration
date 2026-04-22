package com.firefly.domain.configuration.core.config.reference.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.ContractDocumentTypeApi;
import com.firefly.common.reference.master.data.sdk.model.ContractDocumentTypeDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.ContractDocumentTypeDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ContractDocumentTypeConfigService
        extends AbstractReferenceDataService<ContractDocumentTypeApi, ContractDocumentTypeDTO, ContractDocumentTypeDto, UUID> {

    private final ObjectMapper objectMapper;

    public ContractDocumentTypeConfigService(ContractDocumentTypeApi api,
                                             ContractDocumentTypeMapper mapper,
                                             ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.CONTRACT_DOCUMENT_TYPE; }
    @Override protected Mono<ContractDocumentTypeDTO> sdkCreate(ContractDocumentTypeDTO p, String k) { return api.createContractDocumentType(p, k); }
    @Override protected Mono<ContractDocumentTypeDTO> sdkUpdate(UUID id, ContractDocumentTypeDTO p, String k) { return api.updateContractDocumentType(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteContractDocumentType(id, k); }
    @Override protected Mono<ContractDocumentTypeDTO> sdkGetById(UUID id) { return api.getContractDocumentType(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<ContractDocumentTypeDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listContractDocumentTypes(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, ContractDocumentTypeDTO.class));
    }

    @Override protected UUID extractId(ContractDocumentTypeDTO r) { return r.getDocumentTypeId(); }
}
