package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.currency.CurrencyConfigService;
import com.firefly.domain.configuration.web.controller.reference.currency.CurrencyAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CurrencyAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        CurrencyAdminController controller = new CurrencyAdminController(mock(CurrencyConfigService.class));
        assertThat(controller).isNotNull();
    }
}
