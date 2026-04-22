package com.firefly.domain.configuration.web.controller.reference.lookup;

import com.firefly.domain.configuration.core.config.reference.lookup.LookupItemConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.LookupItemDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/lookup-items")
@Tag(name = "Lookup Item Admin", description = "CRUD over lookup items")
public class LookupItemAdminController extends AbstractReferenceDataAdminController<LookupItemDto, UUID> {
    public LookupItemAdminController(LookupItemConfigService service) {
        super(service);
    }
}
