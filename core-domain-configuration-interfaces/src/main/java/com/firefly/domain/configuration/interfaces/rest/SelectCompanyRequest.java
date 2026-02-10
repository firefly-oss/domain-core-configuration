package com.firefly.domain.configuration.interfaces.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SelectCompanyRequest {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("NationalId")
    private String nationalId;

    @JsonProperty("PostCode")
    private String postCode;
}
