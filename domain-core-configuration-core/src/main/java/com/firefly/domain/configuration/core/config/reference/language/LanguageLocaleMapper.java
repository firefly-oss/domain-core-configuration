package com.firefly.domain.configuration.core.config.reference.language;

import com.firefly.common.reference.master.data.sdk.model.LanguageLocaleDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.LanguageLocaleDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LanguageLocaleMapper extends ReferenceDataMapper<LanguageLocaleDto, LanguageLocaleDTO> {
}
