package com.firefly.domain.configuration.core.config.reference.lookup;

import com.firefly.common.reference.master.data.sdk.model.LookupDomainDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.LookupDomainDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LookupDomainMapper extends ReferenceDataMapper<LookupDomainDto, LookupDomainDTO> {
}
