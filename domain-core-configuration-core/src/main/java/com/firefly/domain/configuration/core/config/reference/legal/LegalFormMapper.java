package com.firefly.domain.configuration.core.config.reference.legal;

import com.firefly.common.reference.master.data.sdk.model.LegalFormDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.LegalFormConfigDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LegalFormMapper extends ReferenceDataMapper<LegalFormConfigDto, LegalFormDTO> {
}
