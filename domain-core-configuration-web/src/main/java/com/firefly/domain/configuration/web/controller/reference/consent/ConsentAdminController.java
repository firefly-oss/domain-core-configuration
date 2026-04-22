package com.firefly.domain.configuration.web.controller.reference.consent;

import com.firefly.domain.configuration.core.config.reference.consent.ConsentConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.ConsentDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/consents")
@Tag(name = "Consent Admin", description = "CRUD over consent catalog")
public class ConsentAdminController extends AbstractReferenceDataAdminController<ConsentDto, UUID> {
    public ConsentAdminController(ConsentConfigService service) {
        super(service);
    }
}
