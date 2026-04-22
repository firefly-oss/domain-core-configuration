package com.firefly.domain.configuration.web.controller.reference.asset;

import com.firefly.domain.configuration.core.config.reference.asset.AssetTypeConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.AssetTypeDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/asset-types")
@Tag(name = "Asset Type Admin", description = "CRUD over asset types")
public class AssetTypeAdminController extends AbstractReferenceDataAdminController<AssetTypeDto, UUID> {
    public AssetTypeAdminController(AssetTypeConfigService service) {
        super(service);
    }
}
