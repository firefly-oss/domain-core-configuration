package com.firefly.domain.configuration.core.config.reference.contract;

import com.firefly.common.reference.master.data.sdk.model.ContractDocumentTypeDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.ContractDocumentTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContractDocumentTypeMapper extends ReferenceDataMapper<ContractDocumentTypeDto, ContractDocumentTypeDTO> {
}
