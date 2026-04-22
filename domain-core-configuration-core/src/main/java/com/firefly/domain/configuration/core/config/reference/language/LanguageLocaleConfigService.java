package com.firefly.domain.configuration.core.config.reference.language;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.LanguageLocaleApi;
import com.firefly.common.reference.master.data.sdk.model.LanguageLocaleDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.LanguageLocaleDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class LanguageLocaleConfigService
        extends AbstractReferenceDataService<LanguageLocaleApi, LanguageLocaleDTO, LanguageLocaleDto, UUID> {

    private final ObjectMapper objectMapper;

    public LanguageLocaleConfigService(LanguageLocaleApi api, LanguageLocaleMapper mapper, ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public ReferenceDataEntityType entityType() {
        return ReferenceDataEntityType.LANGUAGE_LOCALE;
    }

    @Override
    protected Mono<LanguageLocaleDTO> sdkCreate(LanguageLocaleDTO payload, String idempotencyKey) {
        return api.createLanguageLocale(payload, idempotencyKey);
    }

    @Override
    protected Mono<LanguageLocaleDTO> sdkUpdate(UUID id, LanguageLocaleDTO payload, String idempotencyKey) {
        return api.updateLanguageLocale(id, payload, idempotencyKey);
    }

    @Override
    protected Mono<Void> sdkDelete(UUID id, String idempotencyKey) {
        return api.deleteLanguageLocale(id, idempotencyKey);
    }

    @Override
    protected Mono<LanguageLocaleDTO> sdkGetById(UUID id) {
        return api.getLanguageLocale(id, UUID.randomUUID().toString());
    }

    @Override
    protected Flux<LanguageLocaleDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listLanguageLocales(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, LanguageLocaleDTO.class));
    }

    @Override
    protected UUID extractId(LanguageLocaleDTO response) {
        return response.getLocaleId();
    }
}
