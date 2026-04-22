package com.firefly.domain.configuration.web.controller.reference.document;

import com.firefly.domain.configuration.core.config.reference.document.DocumentTemplateTypeConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.DocumentTemplateTypeDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/document-template-types")
@Tag(name = "Document Template Type Admin", description = "CRUD over document template types")
public class DocumentTemplateTypeAdminController extends AbstractReferenceDataAdminController<DocumentTemplateTypeDto, UUID> {
    public DocumentTemplateTypeAdminController(DocumentTemplateTypeConfigService service) {
        super(service);
    }
}
