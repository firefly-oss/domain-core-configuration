package com.firefly.domain.configuration.web.controller.reference.currency;

import com.firefly.domain.configuration.core.config.reference.currency.CurrencyConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.CurrencyConfigDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/currencies")
@Tag(name = "Currency Admin", description = "CRUD over master-data currencies")
public class CurrencyAdminController extends AbstractReferenceDataAdminController<CurrencyConfigDto, UUID> {
    public CurrencyAdminController(CurrencyConfigService service) {
        super(service);
    }
}
