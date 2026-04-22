package com.firefly.domain.configuration.core.config.reference.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.NotificationMessageCatalogApi;
import com.firefly.common.reference.master.data.sdk.model.NotificationMessageCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.NotificationMessageDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationMessageConfigService
        extends AbstractReferenceDataService<NotificationMessageCatalogApi, NotificationMessageCatalogDTO, NotificationMessageDto, UUID> {

    private final ObjectMapper objectMapper;

    public NotificationMessageConfigService(NotificationMessageCatalogApi api,
                                            NotificationMessageMapper mapper,
                                            ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.NOTIFICATION_MESSAGE; }
    @Override protected Mono<NotificationMessageCatalogDTO> sdkCreate(NotificationMessageCatalogDTO p, String k) { return api.createNotificationMessage(p, k); }
    @Override protected Mono<NotificationMessageCatalogDTO> sdkUpdate(UUID id, NotificationMessageCatalogDTO p, String k) { return api.updateNotificationMessage(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteNotificationMessage(id, k); }
    @Override protected Mono<NotificationMessageCatalogDTO> sdkGetById(UUID id) { return api.getNotificationMessage(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<NotificationMessageCatalogDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listNotificationMessages(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, NotificationMessageCatalogDTO.class));
    }

    @Override protected UUID extractId(NotificationMessageCatalogDTO r) { return r.getMessageId(); }
}
