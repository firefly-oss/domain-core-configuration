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

import io.fireflysoftware.domain.configuration.infra.dtos.orbis.OrbisCriteria;
import io.fireflysoftware.domain.configuration.infra.dtos.orbis.OrbisMatch;
import io.fireflysoftware.domain.configuration.infra.dtos.orbis.OrbisMatchRequest;
import io.fireflysoftware.domain.configuration.interfaces.rest.SelectCompanyRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrbisMapper {

    @Mapping(target = "match.criteria", source = "query")
    @Mapping(target = "select", expression = "java(getSelectFields())")
    OrbisMatchRequest toOrbisMatchRequest(SelectCompanyRequest query);

    @Mapping(target = "name", source = "name", defaultValue = "")
    @Mapping(target = "city", source = "city", defaultValue = "")
    @Mapping(target = "country", source = "country", defaultValue = "")
    @Mapping(target = "address", source = "address", defaultValue = "")
    @Mapping(target = "emailOrWebsite", constant = "")
    @Mapping(target = "nationalId", source = "nationalId", defaultValue = "")
    @Mapping(target = "phoneOrFax", constant = "")
    @Mapping(target = "postCode", source = "postCode", defaultValue = "")
    @Mapping(target = "state", constant = "")
    @Mapping(target = "ticker", constant = "")
    @Mapping(target = "isin", constant = "")
    @Mapping(target = "bvd9", constant = "")
    OrbisCriteria toOrbisCriteria(SelectCompanyRequest query);

    default List<String> getSelectFields() {
        return List.of(
                "Match.Hint",
                "Match.Score",
                "Match.Name",
                "Match.Name_Local",
                "Match.MatchedName",
                "Match.MatchedName_Type",
                "Match.Address",
                "Match.Postcode",
                "Match.City",
                "Match.Country",
                "Match.Address_Type",
                "Match.PhoneOrFax",
                "Match.EmailOrWebsite",
                "Match.National_Id",
                "Match.NationalIdLabel",
                "Match.State",
                "Match.Region",
                "Match.LegalForm",
                "Match.ConsolidationCode",
                "Match.Status",
                "Match.Ticker",
                "Match.CustomRule",
                "Match.Isin",
                "Match.BvDId"
        );
    }
}
