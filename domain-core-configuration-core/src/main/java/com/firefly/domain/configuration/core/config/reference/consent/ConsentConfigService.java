package com.firefly.domain.configuration.core.config.reference.consent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.ConsentCatalogApi;
import com.firefly.common.reference.master.data.sdk.model.ConsentCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.ConsentDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ConsentConfigService
        extends AbstractReferenceDataService<ConsentCatalogApi, ConsentCatalogDTO, ConsentDto, UUID> {

    private final ObjectMapper objectMapper;

    public ConsentConfigService(ConsentCatalogApi api, ConsentMapper mapper, ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public ReferenceDataEntityType entityType() {
        return ReferenceDataEntityType.CONSENT;
    }

    @Override
    protected Mono<ConsentCatalogDTO> sdkCreate(ConsentCatalogDTO payload, String idempotencyKey) {
        return api.createConsentCatalog(payload, idempotencyKey);
    }

    @Override
    protected Mono<ConsentCatalogDTO> sdkUpdate(UUID id, ConsentCatalogDTO payload, String idempotencyKey) {
        return api.updateConsentCatalog(id, payload, idempotencyKey);
    }

    @Override
    protected Mono<Void> sdkDelete(UUID id, String idempotencyKey) {
        return api.deleteConsentCatalog(id, idempotencyKey);
    }

    @Override
    protected Mono<ConsentCatalogDTO> sdkGetById(UUID id) {
        return api.getConsentCatalog(id, UUID.randomUUID().toString());
    }

    @Override
    protected Flux<ConsentCatalogDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listConsentCatalog(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, ConsentCatalogDTO.class));
    }

    @Override
    protected UUID extractId(ConsentCatalogDTO response) {
        return response.getConsentId();
    }
}
