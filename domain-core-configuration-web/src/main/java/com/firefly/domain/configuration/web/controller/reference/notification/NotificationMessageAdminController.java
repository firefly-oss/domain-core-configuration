package com.firefly.domain.configuration.web.controller.reference.notification;

import com.firefly.domain.configuration.core.config.reference.notification.NotificationMessageConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.NotificationMessageDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/notification-messages")
@Tag(name = "Notification Message Admin", description = "CRUD over notification messages")
public class NotificationMessageAdminController extends AbstractReferenceDataAdminController<NotificationMessageDto, UUID> {
    public NotificationMessageAdminController(NotificationMessageConfigService service) {
        super(service);
    }
}
