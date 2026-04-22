package com.firefly.domain.configuration.core.config.reference.asset;

import com.firefly.common.reference.master.data.sdk.model.AssetTypeDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.AssetTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssetTypeMapper extends ReferenceDataMapper<AssetTypeDto, AssetTypeDTO> {
}
