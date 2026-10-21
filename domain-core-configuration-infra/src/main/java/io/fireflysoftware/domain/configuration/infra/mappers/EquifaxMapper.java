/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.fireflysoftware.domain.configuration.infra.mappers;

import io.fireflysoftware.domain.configuration.infra.dtos.equifax.EquifaxReportResponse;
import io.fireflysoftware.domain.configuration.interfaces.rest.SelectCompanyResponse;
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
