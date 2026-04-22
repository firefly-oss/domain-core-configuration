package com.firefly.domain.configuration.core.config.reference.relationship;

import com.firefly.common.reference.master.data.sdk.model.RelationshipTypeMasterDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.RelationshipTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RelationshipTypeMapper extends ReferenceDataMapper<RelationshipTypeDto, RelationshipTypeMasterDTO> {
}
