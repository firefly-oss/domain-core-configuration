package com.firefly.domain.configuration.web.controller.reference.contract;

import com.firefly.domain.configuration.core.config.reference.contract.ContractDocumentTypeConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.ContractDocumentTypeDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/contract-document-types")
@Tag(name = "Contract Document Type Admin", description = "CRUD over contract document types")
public class ContractDocumentTypeAdminController extends AbstractReferenceDataAdminController<ContractDocumentTypeDto, UUID> {
    public ContractDocumentTypeAdminController(ContractDocumentTypeConfigService service) {
        super(service);
    }
}
