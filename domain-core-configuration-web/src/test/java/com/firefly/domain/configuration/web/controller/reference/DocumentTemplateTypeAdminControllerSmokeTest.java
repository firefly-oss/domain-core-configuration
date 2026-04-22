package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.document.DocumentTemplateTypeConfigService;
import com.firefly.domain.configuration.web.controller.reference.document.DocumentTemplateTypeAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DocumentTemplateTypeAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        DocumentTemplateTypeAdminController controller = new DocumentTemplateTypeAdminController(mock(DocumentTemplateTypeConfigService.class));
        assertThat(controller).isNotNull();
    }
}
