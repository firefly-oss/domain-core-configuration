package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.document.DocumentTemplateConfigService;
import com.firefly.domain.configuration.web.controller.reference.document.DocumentTemplateAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DocumentTemplateAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        DocumentTemplateAdminController controller = new DocumentTemplateAdminController(mock(DocumentTemplateConfigService.class));
        assertThat(controller).isNotNull();
    }
}
