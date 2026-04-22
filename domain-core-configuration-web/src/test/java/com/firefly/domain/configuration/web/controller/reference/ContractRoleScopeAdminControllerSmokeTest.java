package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.contract.ContractRoleScopeConfigService;
import com.firefly.domain.configuration.web.controller.reference.contract.ContractRoleScopeAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ContractRoleScopeAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        ContractRoleScopeAdminController controller = new ContractRoleScopeAdminController(mock(ContractRoleScopeConfigService.class));
        assertThat(controller).isNotNull();
    }
}
