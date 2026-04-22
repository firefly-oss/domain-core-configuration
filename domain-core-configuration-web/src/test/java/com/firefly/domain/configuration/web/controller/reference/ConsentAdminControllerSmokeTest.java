package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.consent.ConsentConfigService;
import com.firefly.domain.configuration.web.controller.reference.consent.ConsentAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ConsentAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        ConsentAdminController controller = new ConsentAdminController(mock(ConsentConfigService.class));
        assertThat(controller).isNotNull();
    }
}
