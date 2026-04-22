package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.contract.ContractTypeConfigService;
import com.firefly.domain.configuration.web.controller.reference.contract.ContractTypeAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ContractTypeAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        ContractTypeAdminController controller = new ContractTypeAdminController(mock(ContractTypeConfigService.class));
        assertThat(controller).isNotNull();
    }
}
