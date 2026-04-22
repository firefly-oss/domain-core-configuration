package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.title.TitleConfigService;
import com.firefly.domain.configuration.web.controller.reference.title.TitleAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TitleAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        TitleAdminController controller = new TitleAdminController(mock(TitleConfigService.class));
        assertThat(controller).isNotNull();
    }
}
