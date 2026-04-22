package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.asset.AssetTypeConfigService;
import com.firefly.domain.configuration.web.controller.reference.asset.AssetTypeAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AssetTypeAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        AssetTypeAdminController controller = new AssetTypeAdminController(mock(AssetTypeConfigService.class));
        assertThat(controller).isNotNull();
    }
}
