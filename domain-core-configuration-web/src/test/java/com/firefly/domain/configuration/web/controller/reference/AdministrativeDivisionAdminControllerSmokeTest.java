package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.geography.AdministrativeDivisionConfigService;
import com.firefly.domain.configuration.web.controller.reference.geography.AdministrativeDivisionAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AdministrativeDivisionAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        AdministrativeDivisionAdminController controller = new AdministrativeDivisionAdminController(mock(AdministrativeDivisionConfigService.class));
        assertThat(controller).isNotNull();
    }
}
