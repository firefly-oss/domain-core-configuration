package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.language.LanguageLocaleConfigService;
import com.firefly.domain.configuration.web.controller.reference.language.LanguageLocaleAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LanguageLocaleAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        LanguageLocaleAdminController controller = new LanguageLocaleAdminController(mock(LanguageLocaleConfigService.class));
        assertThat(controller).isNotNull();
    }
}
