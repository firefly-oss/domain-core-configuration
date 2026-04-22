package com.firefly.domain.configuration.core.config.reference.lookup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.LookupItemsApi;
import com.firefly.common.reference.master.data.sdk.model.LookupItemDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.LookupItemDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class LookupItemConfigService
        extends AbstractReferenceDataService<LookupItemsApi, LookupItemDTO, LookupItemDto, UUID> {

    private final ObjectMapper objectMapper;

    public LookupItemConfigService(LookupItemsApi api, LookupItemMapper mapper, ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.LOOKUP_ITEM; }
    @Override protected Mono<LookupItemDTO> sdkCreate(LookupItemDTO p, String k) { return api.createItem(p, k); }
    @Override protected Mono<LookupItemDTO> sdkUpdate(UUID id, LookupItemDTO p, String k) { return api.updateItem(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteItem(id, k); }
    @Override protected Mono<LookupItemDTO> sdkGetById(UUID id) { return api.getItem(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<LookupItemDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listItems(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, LookupItemDTO.class));
    }

    @Override protected UUID extractId(LookupItemDTO r) { return r.getItemId(); }
}
