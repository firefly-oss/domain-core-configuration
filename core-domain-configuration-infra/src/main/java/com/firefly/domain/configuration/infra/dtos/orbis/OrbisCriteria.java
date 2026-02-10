package com.firefly.domain.configuration.infra.dtos.orbis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "Name",
        "City",
        "Country",
        "Address",
        "EmailOrWebsite",
        "NationalId",
        "PhoneOrFax",
        "PostCode",
        "State",
        "Ticker",
        "Isin",
        "BvD9"
})
public class OrbisCriteria {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("City")
    private String city;
    @JsonProperty("Country")
    private String country;
    @JsonProperty("Address")
    private String address;
    @JsonProperty("EmailOrWebsite")
    private String emailOrWebsite;
    @JsonProperty("NationalId")
    private String nationalId;
    @JsonProperty("PhoneOrFax")
    private String phoneOrFax;
    @JsonProperty("PostCode")
    private String postCode;
    @JsonProperty("State")
    private String state;
    @JsonProperty("Ticker")
    private String ticker;
    @JsonProperty("Isin")
    private String isin;
    @JsonProperty("BvD9")
    private String bvd9;
}
