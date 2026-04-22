package com.firefly.domain.configuration.web.controller.reference.lookup;

import com.firefly.domain.configuration.core.config.reference.lookup.LookupDomainConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.LookupDomainDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/lookup-domains")
@Tag(name = "Lookup Domain Admin", description = "CRUD over lookup domains")
public class LookupDomainAdminController extends AbstractReferenceDataAdminController<LookupDomainDto, UUID> {
    public LookupDomainAdminController(LookupDomainConfigService service) {
        super(service);
    }
}
