package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.activity.ActivityCodeConfigService;
import com.firefly.domain.configuration.web.controller.reference.activity.ActivityCodeAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ActivityCodeAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        ActivityCodeAdminController controller = new ActivityCodeAdminController(mock(ActivityCodeConfigService.class));
        assertThat(controller).isNotNull();
    }
}
