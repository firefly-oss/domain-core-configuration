package com.firefly.domain.configuration.core.config.reference.geography;

import com.firefly.common.reference.master.data.sdk.model.AdministrativeDivisionDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.AdministrativeDivisionDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdministrativeDivisionMapper extends ReferenceDataMapper<AdministrativeDivisionDto, AdministrativeDivisionDTO> {
}
