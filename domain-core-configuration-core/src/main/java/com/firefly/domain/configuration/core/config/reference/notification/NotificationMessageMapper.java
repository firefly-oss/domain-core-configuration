package com.firefly.domain.configuration.core.config.reference.notification;

import com.firefly.common.reference.master.data.sdk.model.NotificationMessageCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.NotificationMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMessageMapper extends ReferenceDataMapper<NotificationMessageDto, NotificationMessageCatalogDTO> {
}
