package com.firefly.domain.configuration.core.config.reference.contract;

import com.firefly.common.reference.master.data.sdk.model.ContractTypeDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.ContractTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContractTypeMapper extends ReferenceDataMapper<ContractTypeDto, ContractTypeDTO> {
}
