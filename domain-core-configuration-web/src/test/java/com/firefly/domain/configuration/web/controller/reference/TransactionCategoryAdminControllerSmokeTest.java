package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.transaction.TransactionCategoryConfigService;
import com.firefly.domain.configuration.web.controller.reference.transaction.TransactionCategoryAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TransactionCategoryAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        TransactionCategoryAdminController controller = new TransactionCategoryAdminController(mock(TransactionCategoryConfigService.class));
        assertThat(controller).isNotNull();
    }
}
