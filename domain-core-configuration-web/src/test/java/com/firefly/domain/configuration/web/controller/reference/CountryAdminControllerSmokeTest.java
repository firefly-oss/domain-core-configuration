package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.geography.CountryConfigService;
import com.firefly.domain.configuration.web.controller.reference.geography.CountryAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CountryAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        CountryAdminController controller = new CountryAdminController(mock(CountryConfigService.class));
        assertThat(controller).isNotNull();
    }
}
