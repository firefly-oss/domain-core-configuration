package com.firefly.domain.configuration.core.config.reference.title;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.TitleMasterApi;
import com.firefly.common.reference.master.data.sdk.model.TitleMasterDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import com.firefly.domain.configuration.interfaces.rest.reference.TitleDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class TitleConfigService
        extends AbstractReferenceDataService<TitleMasterApi, TitleMasterDTO, TitleDto, UUID> {

    private final ObjectMapper objectMapper;

    public TitleConfigService(TitleMasterApi api, TitleMapper mapper, ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public ReferenceDataEntityType entityType() {
        return ReferenceDataEntityType.TITLE;
    }

    @Override
    protected Mono<TitleMasterDTO> sdkCreate(TitleMasterDTO payload, String idempotencyKey) {
        return api.createTitle(payload, idempotencyKey);
    }

    @Override
    protected Mono<TitleMasterDTO> sdkUpdate(UUID id, TitleMasterDTO payload, String idempotencyKey) {
        return api.updateTitle(id, payload, idempotencyKey);
    }

    @Override
    protected Mono<Void> sdkDelete(UUID id, String idempotencyKey) {
        return api.deleteTitle(id, idempotencyKey);
    }

    @Override
    protected Mono<TitleMasterDTO> sdkGetById(UUID id) {
        return api.getTitle(id, UUID.randomUUID().toString());
    }

    @Override
    protected Flux<TitleMasterDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listTitles(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, TitleMasterDTO.class));
    }

    @Override
    protected UUID extractId(TitleMasterDTO response) {
        return response.getTitleId();
    }
}
