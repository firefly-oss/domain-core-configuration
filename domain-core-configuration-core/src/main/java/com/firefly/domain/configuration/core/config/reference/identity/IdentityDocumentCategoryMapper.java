package com.firefly.domain.configuration.core.config.reference.identity;

import com.firefly.common.reference.master.data.sdk.model.IdentityDocumentCategoryCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.IdentityDocumentCategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IdentityDocumentCategoryMapper extends ReferenceDataMapper<IdentityDocumentCategoryDto, IdentityDocumentCategoryCatalogDTO> {
}
