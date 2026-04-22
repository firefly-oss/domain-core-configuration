package com.firefly.domain.configuration.core.config.reference.document;

import com.firefly.common.reference.master.data.sdk.model.DocumentTemplateTypeCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.DocumentTemplateTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentTemplateTypeMapper extends ReferenceDataMapper<DocumentTemplateTypeDto, DocumentTemplateTypeCatalogDTO> {
}
