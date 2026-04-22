package com.firefly.domain.configuration.web.controller.reference.notification;

import com.firefly.domain.configuration.core.config.reference.notification.NotificationMessageTemplateConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.NotificationMessageTemplateDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/notification-message-templates")
@Tag(name = "Notification Message Template Admin", description = "CRUD over notification message templates")
public class NotificationMessageTemplateAdminController extends AbstractReferenceDataAdminController<NotificationMessageTemplateDto, UUID> {
    public NotificationMessageTemplateAdminController(NotificationMessageTemplateConfigService service) {
        super(service);
    }
}
