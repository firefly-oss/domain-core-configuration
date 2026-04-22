package com.firefly.domain.configuration.core.config.reference.lookup;

import com.firefly.common.reference.master.data.sdk.model.LookupItemDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.LookupItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LookupItemMapper extends ReferenceDataMapper<LookupItemDto, LookupItemDTO> {
}
