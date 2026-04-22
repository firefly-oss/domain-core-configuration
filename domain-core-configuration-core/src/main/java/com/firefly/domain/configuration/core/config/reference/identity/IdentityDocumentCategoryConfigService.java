package com.firefly.domain.configuration.core.config.reference.identity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.IdentityDocumentCategoriesApi;
import com.firefly.common.reference.master.data.sdk.model.IdentityDocumentCategoryCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.IdentityDocumentCategoryDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class IdentityDocumentCategoryConfigService
        extends AbstractReferenceDataService<IdentityDocumentCategoriesApi, IdentityDocumentCategoryCatalogDTO, IdentityDocumentCategoryDto, UUID> {

    private final ObjectMapper objectMapper;

    public IdentityDocumentCategoryConfigService(IdentityDocumentCategoriesApi api,
                                                 IdentityDocumentCategoryMapper mapper,
                                                 ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public ReferenceDataEntityType entityType() {
        return ReferenceDataEntityType.IDENTITY_DOCUMENT_CATEGORY;
    }

    @Override
    protected Mono<IdentityDocumentCategoryCatalogDTO> sdkCreate(IdentityDocumentCategoryCatalogDTO payload, String idempotencyKey) {
        return api.createIdentityDocumentCategory(payload, idempotencyKey);
    }

    @Override
    protected Mono<IdentityDocumentCategoryCatalogDTO> sdkUpdate(UUID id, IdentityDocumentCategoryCatalogDTO payload, String idempotencyKey) {
        return api.updateIdentityDocumentCategory(id, payload, idempotencyKey);
    }

    @Override
    protected Mono<Void> sdkDelete(UUID id, String idempotencyKey) {
        return api.deleteIdentityDocumentCategory(id, idempotencyKey);
    }

    @Override
    protected Mono<IdentityDocumentCategoryCatalogDTO> sdkGetById(UUID id) {
        return api.getIdentityDocumentCategory(id, UUID.randomUUID().toString());
    }

    @Override
    protected Flux<IdentityDocumentCategoryCatalogDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listIdentityDocumentCategories(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, IdentityDocumentCategoryCatalogDTO.class));
    }

    @Override
    protected UUID extractId(IdentityDocumentCategoryCatalogDTO response) {
        return response.getCategoryId();
    }
}
