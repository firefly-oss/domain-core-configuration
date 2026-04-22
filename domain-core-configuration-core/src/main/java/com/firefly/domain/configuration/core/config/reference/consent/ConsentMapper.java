package com.firefly.domain.configuration.core.config.reference.consent;

import com.firefly.common.reference.master.data.sdk.model.ConsentCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.ConsentDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConsentMapper extends ReferenceDataMapper<ConsentDto, ConsentCatalogDTO> {
}
