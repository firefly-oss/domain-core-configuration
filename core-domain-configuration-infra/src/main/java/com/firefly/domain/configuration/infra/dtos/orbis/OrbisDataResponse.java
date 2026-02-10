package com.firefly.domain.configuration.infra.dtos.orbis;

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
public class OrbisDataResponse {

    @JsonProperty("Data")
    private List<OrbisCompanyData> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrbisCompanyData {
        @JsonProperty("NAME")
        private String name;
        @JsonProperty("BVD_ID_NUMBER")
        private String bvdIdNumber;
        @JsonProperty("ADDRESS_LINE1")
        private String addressLine1;
        @JsonProperty("POSTCODE")
        private String postcode;
        @JsonProperty("CITY")
        private String city;
        @JsonProperty("COUNTRY")
        private String country;
        @JsonProperty("COUNTRY_ISO_CODE")
        private String countryIsoCode;
        @JsonProperty("LATITUDE")
        private Double latitude;
        @JsonProperty("LONGITUDE")
        private Double longitude;
        @JsonProperty("COUNTRY_REGION")
        private List<String> countryRegion;
        @JsonProperty("COUNTRY_REGION_TYPE")
        private List<String> countryRegionType;
        @JsonProperty("NUTS1")
        private String nuts1;
        @JsonProperty("WORLD_REGION")
        private String worldRegion;
        @JsonProperty("US_STATE")
        private String usState;
        @JsonProperty("COUNTY")
        private String county;
        @JsonProperty("ADDRESS_TYPE")
        private String addressType;
        @JsonProperty("PHONE_NUMBER")
        private List<String> phoneNumber;
        @JsonProperty("WEBSITE")
        private List<String> website;
        @JsonProperty("EMAIL")
        private List<String> email;
        @JsonProperty("TRADE_DESCRIPTION_EN")
        private String tradeDescriptionEn;
        @JsonProperty("PRODUCTS_SERVICES")
        private String productsServices;
        @JsonProperty("INDUSTRY_PRIMARY_CODE")
        private List<String> industryPrimaryCode;
        @JsonProperty("NACE2_MAIN_SECTION")
        private String nace2MainSection;
        @JsonProperty("NACE2_CORE_CODE")
        private String nace2CoreCode;
        @JsonProperty("NACE2_PRIMARY_CODE")
        private List<String> nace2PrimaryCode;
        @JsonProperty("NAICS2022_CORE_CODE")
        private String naics2022CoreCode;
        @JsonProperty("NAICS2022_CORE_LABEL")
        private String naics2022CoreLabel;
        @JsonProperty("USSIC_PRIMARY_CODE")
        private List<String> ussicPrimaryCode;
        @JsonProperty("USSIC_PRIMARY_LABEL")
        private List<String> ussicPrimaryLabel;
        @JsonProperty("OPGSIZE")
        private Long opgsize;
        @JsonProperty("OVERVIEW_PRIMARY_BUSINESS_LINE")
        private String overviewPrimaryBusinessLine;
        @JsonProperty("LEI_STATUS")
        private String leiStatus;
        @JsonProperty("LEI")
        private String lei;
        @JsonProperty("NATIONAL_ID")
        private List<String> nationalId;
        @JsonProperty("NATIONAL_ID_LABEL")
        private List<String> nationalIdLabel;
        @JsonProperty("NATIONAL_ID_TYPE")
        private List<String> nationalIdType;
        @JsonProperty("VAT_NUMBER")
        private List<String> vatNumber;
        @JsonProperty("TRADE_REGISTER_NUMBER")
        private List<String> tradeRegisterNumber;
        @JsonProperty("TIN")
        private String tin;
        @JsonProperty("SWIFT_CODE")
        private String swiftCode;
        @JsonProperty("ISIN")
        private String isin;
        @JsonProperty("GRID_MATCH_INDICATOR")
        private Boolean gridMatchIndicator;
        @JsonProperty("GRID_MATCH_SANCTIONS_INDICATOR")
        private Boolean gridMatchSanctionsIndicator;
        @JsonProperty("GRID_MATCH_WATCHLIST_INDICATOR")
        private Boolean gridMatchWatchlistIndicator;
        @JsonProperty("GRID_MATCH_PEP_INDICATOR")
        private Boolean gridMatchPepIndicator;
        @JsonProperty("GRID_MATCH_MEDIA_INDICATOR")
        private Boolean gridMatchMediaIndicator;
    }
}
