package com.firefly.domain.configuration.core.config.reference.identity;

import com.firefly.common.reference.master.data.sdk.model.IdentityDocumentCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.IdentityDocumentDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IdentityDocumentMapper extends ReferenceDataMapper<IdentityDocumentDto, IdentityDocumentCatalogDTO> {
}
