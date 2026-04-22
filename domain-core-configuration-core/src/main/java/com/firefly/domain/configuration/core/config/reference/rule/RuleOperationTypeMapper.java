package com.firefly.domain.configuration.core.config.reference.rule;

import com.firefly.common.reference.master.data.sdk.model.RuleOperationTypeDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.RuleOperationTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RuleOperationTypeMapper extends ReferenceDataMapper<RuleOperationTypeDto, RuleOperationTypeDTO> {
}
