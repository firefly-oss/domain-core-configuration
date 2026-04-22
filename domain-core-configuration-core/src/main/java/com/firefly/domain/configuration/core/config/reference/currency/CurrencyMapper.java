package com.firefly.domain.configuration.core.config.reference.currency;

import com.firefly.common.reference.master.data.sdk.model.CurrencyDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.CurrencyConfigDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CurrencyMapper extends ReferenceDataMapper<CurrencyConfigDto, CurrencyDTO> {
}
