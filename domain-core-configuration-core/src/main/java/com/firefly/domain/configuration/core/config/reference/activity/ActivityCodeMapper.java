package com.firefly.domain.configuration.core.config.reference.activity;

import com.firefly.common.reference.master.data.sdk.model.ActivityCodeDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.ActivityCodeDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActivityCodeMapper extends ReferenceDataMapper<ActivityCodeDto, ActivityCodeDTO> {
}
