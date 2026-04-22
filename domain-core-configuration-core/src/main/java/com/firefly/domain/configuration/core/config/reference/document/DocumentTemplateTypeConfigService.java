package com.firefly.domain.configuration.core.config.reference.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.DocumentTemplateTypesApi;
import com.firefly.common.reference.master.data.sdk.model.DocumentTemplateTypeCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.DocumentTemplateTypeDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentTemplateTypeConfigService
        extends AbstractReferenceDataService<DocumentTemplateTypesApi, DocumentTemplateTypeCatalogDTO, DocumentTemplateTypeDto, UUID> {

    private final ObjectMapper objectMapper;

    public DocumentTemplateTypeConfigService(DocumentTemplateTypesApi api,
                                             DocumentTemplateTypeMapper mapper,
                                             ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.DOCUMENT_TEMPLATE_TYPE; }
    @Override protected Mono<DocumentTemplateTypeCatalogDTO> sdkCreate(DocumentTemplateTypeCatalogDTO p, String k) { return api.createDocumentTemplateType(p, k); }
    @Override protected Mono<DocumentTemplateTypeCatalogDTO> sdkUpdate(UUID id, DocumentTemplateTypeCatalogDTO p, String k) { return api.updateDocumentTemplateType(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteDocumentTemplateType(id, k); }
    @Override protected Mono<DocumentTemplateTypeCatalogDTO> sdkGetById(UUID id) { return api.getDocumentTemplateType(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<DocumentTemplateTypeCatalogDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listDocumentTemplateTypes(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, DocumentTemplateTypeCatalogDTO.class));
    }

    @Override protected UUID extractId(DocumentTemplateTypeCatalogDTO r) { return r.getTypeId(); }
}
