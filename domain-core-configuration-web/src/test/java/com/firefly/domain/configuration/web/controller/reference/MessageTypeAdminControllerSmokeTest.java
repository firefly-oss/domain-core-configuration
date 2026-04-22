package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.notification.MessageTypeConfigService;
import com.firefly.domain.configuration.web.controller.reference.notification.MessageTypeAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MessageTypeAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        MessageTypeAdminController controller = new MessageTypeAdminController(mock(MessageTypeConfigService.class));
        assertThat(controller).isNotNull();
    }
}
