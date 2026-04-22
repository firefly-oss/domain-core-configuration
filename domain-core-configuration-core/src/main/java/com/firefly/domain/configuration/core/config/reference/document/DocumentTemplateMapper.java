package com.firefly.domain.configuration.core.config.reference.document;

import com.firefly.common.reference.master.data.sdk.model.DocumentTemplateCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.DocumentTemplateDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentTemplateMapper extends ReferenceDataMapper<DocumentTemplateDto, DocumentTemplateCatalogDTO> {
}
