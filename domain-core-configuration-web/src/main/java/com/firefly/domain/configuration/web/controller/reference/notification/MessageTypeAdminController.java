package com.firefly.domain.configuration.web.controller.reference.notification;

import com.firefly.domain.configuration.core.config.reference.notification.MessageTypeConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.MessageTypeDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/message-types")
@Tag(name = "Message Type Admin", description = "CRUD over message type catalog")
public class MessageTypeAdminController extends AbstractReferenceDataAdminController<MessageTypeDto, UUID> {
    public MessageTypeAdminController(MessageTypeConfigService service) {
        super(service);
    }
}
