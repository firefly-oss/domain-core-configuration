package com.firefly.domain.configuration.web.controller.reference.geography;

import com.firefly.domain.configuration.core.config.reference.geography.AdministrativeDivisionConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.AdministrativeDivisionDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/administrative-divisions")
@Tag(name = "Administrative Division Admin", description = "CRUD over administrative divisions")
public class AdministrativeDivisionAdminController extends AbstractReferenceDataAdminController<AdministrativeDivisionDto, UUID> {
    public AdministrativeDivisionAdminController(AdministrativeDivisionConfigService service) {
        super(service);
    }
}
