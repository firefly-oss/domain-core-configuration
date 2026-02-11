package com.firefly.domain.configuration.interfaces.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SelectCompanyResponse {

    @JsonProperty("Hint")
    private String hint;

    @JsonProperty("Score")
    private Double score;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Name_Local")
    private String nameLocal;

    @JsonProperty("MatchedName")
    private String matchedName;

    @JsonProperty("MatchedName_Type")
    private String matchedNameType;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("Postcode")
    private String postcode;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("Address_Type")
    private String addressType;

    @JsonProperty("PhoneOrFax")
    private List<String> phoneOrFax;

    @JsonProperty("EmailOrWebsite")
    private List<String> emailOrWebsite;

    @JsonProperty("National_Id")
    private List<String> nationalId;

    @JsonProperty("NationalIdLabel")
    private List<String> nationalIdLabel;

    @JsonProperty("State")
    private String state;

    @JsonProperty("Region")
    private String region;

    @JsonProperty("LegalForm")
    private String legalForm;

    @JsonProperty("ConsolidationCode")
    private String consolidationCode;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Ticker")
    private String ticker;

    @JsonProperty("CustomRule")
    private String customRule;

    @JsonProperty("Isin")
    private String isin;

    @JsonProperty("BvDId")
    private String bvdId;
}
