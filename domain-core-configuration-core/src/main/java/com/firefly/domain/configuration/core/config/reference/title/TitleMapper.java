package com.firefly.domain.configuration.core.config.reference.title;

import com.firefly.common.reference.master.data.sdk.model.TitleMasterDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.TitleDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TitleMapper extends ReferenceDataMapper<TitleDto, TitleMasterDTO> {
}
