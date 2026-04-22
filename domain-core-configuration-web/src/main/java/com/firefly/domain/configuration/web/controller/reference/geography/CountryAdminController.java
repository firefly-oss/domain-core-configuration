package com.firefly.domain.configuration.web.controller.reference.geography;

import com.firefly.domain.configuration.core.config.reference.geography.CountryConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.CountryConfigDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/countries")
@Tag(name = "Country Admin", description = "CRUD over master-data countries")
public class CountryAdminController extends AbstractReferenceDataAdminController<CountryConfigDto, UUID> {
    public CountryAdminController(CountryConfigService service) {
        super(service);
    }
}
