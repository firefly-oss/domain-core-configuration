package com.firefly.domain.configuration.web.controller.reference.bank;

import com.firefly.domain.configuration.core.config.reference.bank.BankConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.BankDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/banks")
@Tag(name = "Bank Admin", description = "CRUD over master-data bank institution codes")
public class BankAdminController extends AbstractReferenceDataAdminController<BankDto, UUID> {
    public BankAdminController(BankConfigService service) {
        super(service);
    }
}
