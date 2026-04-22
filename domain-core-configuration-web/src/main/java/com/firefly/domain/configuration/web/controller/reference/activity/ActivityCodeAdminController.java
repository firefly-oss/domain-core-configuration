package com.firefly.domain.configuration.web.controller.reference.activity;

import com.firefly.domain.configuration.core.config.reference.activity.ActivityCodeConfigService;
import com.firefly.domain.configuration.interfaces.rest.reference.ActivityCodeDto;
import com.firefly.domain.configuration.web.controller.reference.base.AbstractReferenceDataAdminController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuration/admin/activity-codes")
@Tag(name = "Activity Code Admin", description = "CRUD over activity codes")
public class ActivityCodeAdminController extends AbstractReferenceDataAdminController<ActivityCodeDto, UUID> {
    public ActivityCodeAdminController(ActivityCodeConfigService service) {
        super(service);
    }
}
