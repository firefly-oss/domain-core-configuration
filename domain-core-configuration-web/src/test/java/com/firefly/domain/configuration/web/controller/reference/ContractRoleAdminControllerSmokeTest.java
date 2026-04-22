package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.contract.ContractRoleConfigService;
import com.firefly.domain.configuration.web.controller.reference.contract.ContractRoleAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ContractRoleAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        ContractRoleAdminController controller = new ContractRoleAdminController(mock(ContractRoleConfigService.class));
        assertThat(controller).isNotNull();
    }
}
