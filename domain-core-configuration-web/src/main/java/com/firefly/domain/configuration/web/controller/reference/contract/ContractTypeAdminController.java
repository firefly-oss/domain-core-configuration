package com.firefly.domain.configuration.web.controller.reference.contract;

import com.firefly.domain.configuration.core.config.reference.contract.ContractTypeConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.ContractTypeDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/contract-types")
@Tag(name = "Contract Type Admin", description = "CRUD over contract types")
public class ContractTypeAdminController extends AbstractReferenceDataAdminController<ContractTypeDto, UUID> {
    public ContractTypeAdminController(ContractTypeConfigService service) {
        super(service);
    }
}
