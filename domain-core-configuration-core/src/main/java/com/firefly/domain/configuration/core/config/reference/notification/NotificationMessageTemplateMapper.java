package com.firefly.domain.configuration.core.config.reference.notification;

import com.firefly.common.reference.master.data.sdk.model.NotificationMessageTemplateDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.NotificationMessageTemplateDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMessageTemplateMapper extends ReferenceDataMapper<NotificationMessageTemplateDto, NotificationMessageTemplateDTO> {
}
