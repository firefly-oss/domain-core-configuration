package com.firefly.domain.configuration.web.controller.reference.legal;

import com.firefly.domain.configuration.core.config.reference.legal.LegalFormConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.LegalFormConfigDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/legal-forms")
@Tag(name = "Legal Form Admin", description = "CRUD over master-data legal forms")
public class LegalFormAdminController extends AbstractReferenceDataAdminController<LegalFormConfigDto, UUID> {
    public LegalFormAdminController(LegalFormConfigService service) {
        super(service);
    }
}
