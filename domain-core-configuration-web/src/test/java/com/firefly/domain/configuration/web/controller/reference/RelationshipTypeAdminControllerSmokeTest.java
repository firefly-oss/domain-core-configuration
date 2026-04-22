package com.firefly.domain.configuration.web.controller.reference;

import com.firefly.domain.configuration.core.config.reference.relationship.RelationshipTypeConfigService;
import com.firefly.domain.configuration.web.controller.reference.relationship.RelationshipTypeAdminController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RelationshipTypeAdminControllerSmokeTest {

    @Test
    void controllerIsInstantiable() {
        RelationshipTypeAdminController controller = new RelationshipTypeAdminController(mock(RelationshipTypeConfigService.class));
        assertThat(controller).isNotNull();
    }
}
