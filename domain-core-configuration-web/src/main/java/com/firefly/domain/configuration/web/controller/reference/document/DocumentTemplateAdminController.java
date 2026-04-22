package com.firefly.domain.configuration.web.controller.reference.document;

import com.firefly.domain.configuration.core.config.reference.document.DocumentTemplateConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.DocumentTemplateDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/document-templates")
@Tag(name = "Document Template Admin", description = "CRUD over document templates")
public class DocumentTemplateAdminController extends AbstractReferenceDataAdminController<DocumentTemplateDto, UUID> {
    public DocumentTemplateAdminController(DocumentTemplateConfigService service) {
        super(service);
    }
}
