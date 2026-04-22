package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.bank.BankConfigService;
import com.firefly.domain.configuration.web.controller.reference.bank.BankAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class BankAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        BankAdminController controller = new BankAdminController(mock(BankConfigService.class));
        assertThat(controller).isNotNull();
    }
}
