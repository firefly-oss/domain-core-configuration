package com.firefly.domain.configuration.web.controller.reference.contract;

import com.firefly.domain.configuration.core.config.reference.contract.ContractRoleConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.ContractRoleDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/contract-roles")
@Tag(name = "Contract Role Admin", description = "CRUD over contract roles")
public class ContractRoleAdminController extends AbstractReferenceDataAdminController<ContractRoleDto, UUID> {
    public ContractRoleAdminController(ContractRoleConfigService service) {
        super(service);
    }
}
