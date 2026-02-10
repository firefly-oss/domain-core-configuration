package com.firefly.domain.configuration.infra.mappers;

import com.firefly.domain.configuration.infra.dtos.orbis.OrbisCriteria;
import com.firefly.domain.configuration.infra.dtos.orbis.OrbisMatch;
import com.firefly.domain.configuration.infra.dtos.orbis.OrbisMatchRequest;
import com.firefly.domain.configuration.interfaces.rest.SelectCompanyRequest;
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
