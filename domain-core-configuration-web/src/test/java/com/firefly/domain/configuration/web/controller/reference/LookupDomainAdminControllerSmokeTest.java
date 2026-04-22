package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.lookup.LookupDomainConfigService;
import com.firefly.domain.configuration.web.controller.reference.lookup.LookupDomainAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LookupDomainAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        LookupDomainAdminController controller = new LookupDomainAdminController(mock(LookupDomainConfigService.class));
        assertThat(controller).isNotNull();
    }
}
