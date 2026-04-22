package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.legal.LegalFormConfigService;
import com.firefly.domain.configuration.web.controller.reference.legal.LegalFormAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LegalFormAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        LegalFormAdminController controller = new LegalFormAdminController(mock(LegalFormConfigService.class));
        assertThat(controller).isNotNull();
    }
}
