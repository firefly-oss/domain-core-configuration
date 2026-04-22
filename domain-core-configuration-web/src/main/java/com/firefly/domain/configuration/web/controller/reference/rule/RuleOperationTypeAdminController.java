package com.firefly.domain.configuration.web.controller.reference.rule;

import com.firefly.domain.configuration.core.config.reference.rule.RuleOperationTypeConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.RuleOperationTypeDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/rule-operation-types")
@Tag(name = "Rule Operation Type Admin", description = "CRUD over rule operation types")
public class RuleOperationTypeAdminController extends AbstractReferenceDataAdminController<RuleOperationTypeDto, UUID> {
    public RuleOperationTypeAdminController(RuleOperationTypeConfigService service) {
        super(service);
    }
}
