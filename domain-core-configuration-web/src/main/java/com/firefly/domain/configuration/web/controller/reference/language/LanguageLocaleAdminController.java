package com.firefly.domain.configuration.web.controller.reference.language;

import com.firefly.domain.configuration.core.config.reference.language.LanguageLocaleConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.LanguageLocaleDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/language-locales")
@Tag(name = "Language Locale Admin", description = "CRUD over master-data language locales")
public class LanguageLocaleAdminController extends AbstractReferenceDataAdminController<LanguageLocaleDto, UUID> {
    public LanguageLocaleAdminController(LanguageLocaleConfigService service) {
        super(service);
    }
}
