package com.firefly.domain.configuration.web.controller.reference.relationship;

import com.firefly.domain.configuration.core.config.reference.relationship.RelationshipTypeConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.RelationshipTypeDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/relationship-types")
@Tag(name = "Relationship Type Admin", description = "CRUD over relationship type master")
public class RelationshipTypeAdminController extends AbstractReferenceDataAdminController<RelationshipTypeDto, UUID> {
    public RelationshipTypeAdminController(RelationshipTypeConfigService service) {
        super(service);
    }
}
