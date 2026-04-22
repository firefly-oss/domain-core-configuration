package com.firefly.domain.configuration.core.config.reference.transaction;

import com.firefly.common.reference.master.data.sdk.model.TransactionCategoryCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.TransactionCategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionCategoryMapper extends ReferenceDataMapper<TransactionCategoryDto, TransactionCategoryCatalogDTO> {
}
