package com.firefly.domain.configuration.web.controller.reference.title;

import com.firefly.domain.configuration.core.config.reference.title.TitleConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.TitleDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/titles")
@Tag(name = "Title Admin", description = "CRUD over master-data personal titles")
public class TitleAdminController extends AbstractReferenceDataAdminController<TitleDto, UUID> {
    public TitleAdminController(TitleConfigService service) {
        super(service);
    }
}
