package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.notification.NotificationMessageTemplateConfigService;
import com.firefly.domain.configuration.web.controller.reference.notification.NotificationMessageTemplateAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NotificationMessageTemplateAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        NotificationMessageTemplateAdminController controller = new NotificationMessageTemplateAdminController(mock(NotificationMessageTemplateConfigService.class));
        assertThat(controller).isNotNull();
    }
}
