package com.firefly.domain.configuration.web.controller.reference.identity;

import com.firefly.domain.configuration.core.config.reference.identity.IdentityDocumentConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.IdentityDocumentDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/identity-documents")
@Tag(name = "Identity Document Admin", description = "CRUD over identity documents")
public class IdentityDocumentAdminController extends AbstractReferenceDataAdminController<IdentityDocumentDto, UUID> {
    public IdentityDocumentAdminController(IdentityDocumentConfigService service) {
        super(service);
    }
}
