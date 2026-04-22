package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.contract.ContractDocumentTypeConfigService;
import com.firefly.domain.configuration.web.controller.reference.contract.ContractDocumentTypeAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ContractDocumentTypeAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        ContractDocumentTypeAdminController controller = new ContractDocumentTypeAdminController(mock(ContractDocumentTypeConfigService.class));
        assertThat(controller).isNotNull();
    }
}
