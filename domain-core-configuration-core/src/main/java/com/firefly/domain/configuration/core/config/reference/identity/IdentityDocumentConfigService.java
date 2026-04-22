package com.firefly.domain.configuration.core.config.reference.identity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.IdentityDocumentsApi;
import com.firefly.common.reference.master.data.sdk.model.IdentityDocumentCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.IdentityDocumentDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class IdentityDocumentConfigService
        extends AbstractReferenceDataService<IdentityDocumentsApi, IdentityDocumentCatalogDTO, IdentityDocumentDto, UUID> {

    private final ObjectMapper objectMapper;

    public IdentityDocumentConfigService(IdentityDocumentsApi api, IdentityDocumentMapper mapper, ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public ReferenceDataEntityType entityType() {
        return ReferenceDataEntityType.IDENTITY_DOCUMENT;
    }

    @Override
    protected Mono<IdentityDocumentCatalogDTO> sdkCreate(IdentityDocumentCatalogDTO payload, String idempotencyKey) {
        return api.createIdentityDocument(payload, idempotencyKey);
    }

    @Override
    protected Mono<IdentityDocumentCatalogDTO> sdkUpdate(UUID id, IdentityDocumentCatalogDTO payload, String idempotencyKey) {
        return api.updateIdentityDocument(id, payload, idempotencyKey);
    }

    @Override
    protected Mono<Void> sdkDelete(UUID id, String idempotencyKey) {
        return api.deleteIdentityDocument(id, idempotencyKey);
    }

    @Override
    protected Mono<IdentityDocumentCatalogDTO> sdkGetById(UUID id) {
        return api.getIdentityDocument(id, UUID.randomUUID().toString());
    }

    @Override
    protected Flux<IdentityDocumentCatalogDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listIdentityDocuments(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, IdentityDocumentCatalogDTO.class));
    }

    @Override
    protected UUID extractId(IdentityDocumentCatalogDTO response) {
        return response.getDocumentId();
    }
}
