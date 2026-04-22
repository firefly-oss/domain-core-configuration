package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.identity.IdentityDocumentConfigService;
import com.firefly.domain.configuration.web.controller.reference.identity.IdentityDocumentAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class IdentityDocumentAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        IdentityDocumentAdminController controller = new IdentityDocumentAdminController(mock(IdentityDocumentConfigService.class));
        assertThat(controller).isNotNull();
    }
}
