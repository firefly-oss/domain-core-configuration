package com.firefly.domain.configuration.core.config.reference.contract;

import com.firefly.common.reference.master.data.sdk.model.ContractRoleDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.ContractRoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContractRoleMapper extends ReferenceDataMapper<ContractRoleDto, ContractRoleDTO> {
}
