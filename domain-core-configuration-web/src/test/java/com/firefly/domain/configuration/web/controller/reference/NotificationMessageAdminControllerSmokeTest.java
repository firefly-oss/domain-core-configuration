package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.notification.NotificationMessageConfigService;
import com.firefly.domain.configuration.web.controller.reference.notification.NotificationMessageAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NotificationMessageAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        NotificationMessageAdminController controller = new NotificationMessageAdminController(mock(NotificationMessageConfigService.class));
        assertThat(controller).isNotNull();
    }
}
