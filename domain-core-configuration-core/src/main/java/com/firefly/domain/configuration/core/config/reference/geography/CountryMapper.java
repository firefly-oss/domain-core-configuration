package com.firefly.domain.configuration.core.config.reference.geography;

import com.firefly.common.reference.master.data.sdk.model.CountryDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.CountryConfigDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CountryMapper extends ReferenceDataMapper<CountryConfigDto, CountryDTO> {
}
