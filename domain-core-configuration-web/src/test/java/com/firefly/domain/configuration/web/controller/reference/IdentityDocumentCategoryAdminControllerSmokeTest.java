package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.identity.IdentityDocumentCategoryConfigService;
import com.firefly.domain.configuration.web.controller.reference.identity.IdentityDocumentCategoryAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class IdentityDocumentCategoryAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        IdentityDocumentCategoryAdminController controller = new IdentityDocumentCategoryAdminController(mock(IdentityDocumentCategoryConfigService.class));
        assertThat(controller).isNotNull();
    }
}
