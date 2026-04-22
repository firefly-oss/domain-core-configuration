package com.firefly.domain.configuration.core.config.reference.bank;

import com.firefly.common.reference.master.data.sdk.model.BankInstitutionCodeDTO;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.BankDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankMapper extends ReferenceDataMapper<BankDto, BankInstitutionCodeDTO> {
}
