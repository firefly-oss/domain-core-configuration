package com.firefly.domain.configuration.web.controller.reference.identity;

import com.firefly.domain.configuration.core.config.reference.identity.IdentityDocumentCategoryConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.IdentityDocumentCategoryDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/identity-document-categories")
@Tag(name = "Identity Document Category Admin", description = "CRUD over identity document categories")
public class IdentityDocumentCategoryAdminController extends AbstractReferenceDataAdminController<IdentityDocumentCategoryDto, UUID> {
    public IdentityDocumentCategoryAdminController(IdentityDocumentCategoryConfigService service) {
        super(service);
    }
}
