package com.firefly.domain.configuration.core.config.reference.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.DocumentTemplatesApi;
import com.firefly.common.reference.master.data.sdk.model.DocumentTemplateCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.DocumentTemplateDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentTemplateConfigService
        extends AbstractReferenceDataService<DocumentTemplatesApi, DocumentTemplateCatalogDTO, DocumentTemplateDto, UUID> {

    private final ObjectMapper objectMapper;

    public DocumentTemplateConfigService(DocumentTemplatesApi api,
                                         DocumentTemplateMapper mapper,
                                         ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.DOCUMENT_TEMPLATE; }
    @Override protected Mono<DocumentTemplateCatalogDTO> sdkCreate(DocumentTemplateCatalogDTO p, String k) { return api.createDocumentTemplate(p, k); }
    @Override protected Mono<DocumentTemplateCatalogDTO> sdkUpdate(UUID id, DocumentTemplateCatalogDTO p, String k) { return api.updateDocumentTemplate(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteDocumentTemplate(id, k); }
    @Override protected Mono<DocumentTemplateCatalogDTO> sdkGetById(UUID id) { return api.getDocumentTemplate(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<DocumentTemplateCatalogDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listDocumentTemplates(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, DocumentTemplateCatalogDTO.class));
    }

    @Override protected UUID extractId(DocumentTemplateCatalogDTO r) { return r.getTemplateId(); }
}
