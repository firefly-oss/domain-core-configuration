package com.firefly.domain.configuration.core.config.reference.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.MessageTypeCatalogApi;
import com.firefly.common.reference.master.data.sdk.model.MessageTypeCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.MessageTypeDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class MessageTypeConfigService
        extends AbstractReferenceDataService<MessageTypeCatalogApi, MessageTypeCatalogDTO, MessageTypeDto, UUID> {

    private final ObjectMapper objectMapper;

    public MessageTypeConfigService(MessageTypeCatalogApi api,
                                    MessageTypeMapper mapper,
                                    ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.MESSAGE_TYPE; }
    @Override protected Mono<MessageTypeCatalogDTO> sdkCreate(MessageTypeCatalogDTO p, String k) { return api.createMessageType(p, k); }
    @Override protected Mono<MessageTypeCatalogDTO> sdkUpdate(UUID id, MessageTypeCatalogDTO p, String k) { return api.updateMessageType(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteMessageType(id, k); }
    @Override protected Mono<MessageTypeCatalogDTO> sdkGetById(UUID id) { return api.getMessageType(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<MessageTypeCatalogDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listMessageTypes(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, MessageTypeCatalogDTO.class));
    }

    @Override protected UUID extractId(MessageTypeCatalogDTO r) { return r.getTypeId(); }
}
