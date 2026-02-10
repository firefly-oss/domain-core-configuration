package com.firefly.domain.configuration.infra.mappers;

import com.firefly.domain.configuration.infra.dtos.equifax.EquifaxReportResponse;
import com.firefly.domain.configuration.interfaces.rest.SelectCompanyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EquifaxMapper {

    @Mapping(target = "name", expression = "java(mapName(response))")
    @Mapping(target = "address", expression = "java(mapAddress(response))")
    @Mapping(target = "city", expression = "java(mapCity(response))")
    @Mapping(target = "state", expression = "java(mapState(response))")
    @Mapping(target = "postcode", expression = "java(mapPostcode(response))")
    @Mapping(target = "nationalId", expression = "java(mapNationalId(response))")
    @Mapping(target = "hint", expression = "java(\"Equifax Report Processed: \" + response.getStatus())")
    SelectCompanyResponse toSelectCompanyResponse(EquifaxReportResponse response);

    default EquifaxReportResponse.EquifaxUSConsumerCreditReport getFirstReport(EquifaxReportResponse response) {
        if (response == null || response.getConsumers() == null || 
            response.getConsumers().getEquifaxUSConsumerCreditReport() == null || 
            response.getConsumers().getEquifaxUSConsumerCreditReport().isEmpty()) {
            return null;
        }
        return response.getConsumers().getEquifaxUSConsumerCreditReport().get(0);
    }

    default EquifaxReportResponse.Addresses getFirstAddress(EquifaxReportResponse response) {
        var report = getFirstReport(response);
        if (report == null || report.getAddresses() == null || report.getAddresses().isEmpty()) {
            return null;
        }
        return report.getAddresses().get(0);
    }

    default String mapName(EquifaxReportResponse response) {
        var report = getFirstReport(response);
        if (report == null || report.getSubjectName() == null) {
            return null;
        }
        var subject = report.getSubjectName();
        return subject.getFirstName() + " " + subject.getLastName();
    }

    default String mapAddress(EquifaxReportResponse response) {
        var address = getFirstAddress(response);
        return address != null ? address.getAddressLine1() : null;
    }

    default String mapCity(EquifaxReportResponse response) {
        var address = getFirstAddress(response);
        return address != null ? address.getCityName() : null;
    }

    default String mapState(EquifaxReportResponse response) {
        var address = getFirstAddress(response);
        return address != null ? address.getStateAbbreviation() : null;
    }

    default String mapPostcode(EquifaxReportResponse response) {
        var address = getFirstAddress(response);
        return address != null && address.getZipCode() != null ? address.getZipCode().toString() : null;
    }

    default List<String> mapNationalId(EquifaxReportResponse response) {
        var report = getFirstReport(response);
        if (report == null) {
            return null;
        }
        return report.getSubjectSocialNum() != null ? List.of(report.getSubjectSocialNum().toString()) : null;
    }
}
