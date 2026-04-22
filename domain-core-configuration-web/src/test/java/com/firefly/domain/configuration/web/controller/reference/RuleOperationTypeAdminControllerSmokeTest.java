package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.rule.RuleOperationTypeConfigService;
import com.firefly.domain.configuration.web.controller.reference.rule.RuleOperationTypeAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RuleOperationTypeAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        RuleOperationTypeAdminController controller = new RuleOperationTypeAdminController(mock(RuleOperationTypeConfigService.class));
        assertThat(controller).isNotNull();
    }
}
