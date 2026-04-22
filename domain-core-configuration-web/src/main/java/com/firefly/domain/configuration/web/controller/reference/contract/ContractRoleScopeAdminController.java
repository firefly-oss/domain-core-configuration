package com.firefly.domain.configuration.web.controller.reference.contract;

import com.firefly.domain.configuration.core.config.reference.contract.ContractRoleScopeConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.ContractRoleScopeDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/contract-role-scopes")
@Tag(name = "Contract Role Scope Admin", description = "CRUD over contract role scopes")
public class ContractRoleScopeAdminController extends AbstractReferenceDataAdminController<ContractRoleScopeDto, UUID> {
    public ContractRoleScopeAdminController(ContractRoleScopeConfigService service) {
        super(service);
    }
}
