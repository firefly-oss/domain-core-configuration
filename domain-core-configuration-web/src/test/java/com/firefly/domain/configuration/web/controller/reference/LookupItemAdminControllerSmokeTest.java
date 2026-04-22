package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.lookup.LookupItemConfigService;
import com.firefly.domain.configuration.web.controller.reference.lookup.LookupItemAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LookupItemAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        LookupItemAdminController controller = new LookupItemAdminController(mock(LookupItemConfigService.class));
        assertThat(controller).isNotNull();
    }
}
