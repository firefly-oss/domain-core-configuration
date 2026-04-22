package com.firefly.domain.configuration.core.config.reference.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.NotificationMessageTemplatesApi;
import com.firefly.common.reference.master.data.sdk.model.NotificationMessageTemplateDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.NotificationMessageTemplateDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationMessageTemplateConfigService
        extends AbstractReferenceDataService<NotificationMessageTemplatesApi, NotificationMessageTemplateDTO, NotificationMessageTemplateDto, UUID> {

    private final ObjectMapper objectMapper;

    public NotificationMessageTemplateConfigService(NotificationMessageTemplatesApi api,
                                                    NotificationMessageTemplateMapper mapper,
                                                    ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.NOTIFICATION_MESSAGE_TEMPLATE; }
    @Override protected Mono<NotificationMessageTemplateDTO> sdkCreate(NotificationMessageTemplateDTO p, String k) { return api.createNotificationMessageTemplate(p, k); }
    @Override protected Mono<NotificationMessageTemplateDTO> sdkUpdate(UUID id, NotificationMessageTemplateDTO p, String k) { return api.updateNotificationMessageTemplate(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteNotificationMessageTemplate(id, k); }
    @Override protected Mono<NotificationMessageTemplateDTO> sdkGetById(UUID id) { return api.getNotificationMessageTemplate(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<NotificationMessageTemplateDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listNotificationMessageTemplates(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, NotificationMessageTemplateDTO.class));
    }

    @Override protected UUID extractId(NotificationMessageTemplateDTO r) { return r.getTemplateId(); }
}
