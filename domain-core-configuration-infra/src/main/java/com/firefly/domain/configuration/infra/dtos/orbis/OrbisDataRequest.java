package com.firefly.domain.configuration.infra.dtos.orbis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrbisDataRequest {
    @JsonProperty("WHERE")
    private List<Map<String, List<String>>> where;
    @JsonProperty("SELECT")
    private List<Map<String, Object>> select;

    public static OrbisDataRequest forBvDId(String bvdId) {
        return OrbisDataRequest.builder()
                .where(List.of(Map.of("BvDID", List.of(bvdId))))
                .select(List.of(
                        Map.of("NAME", Map.of("AS", "NAME")),
                        Map.of("BVD_ID_NUMBER", Map.of("AS", "BVD_ID_NUMBER")),
                        Map.of("ADDRESS_LINE1", Map.of("AS", "ADDRESS_LINE1")),
                        Map.of("POSTCODE", Map.of("AS", "POSTCODE")),
                        Map.of("CITY", Map.of("AS", "CITY")),
                        Map.of("COUNTRY", Map.of("AS", "COUNTRY")),
                        Map.of("COUNTRY_ISO_CODE", Map.of("AS", "COUNTRY_ISO_CODE")),
                        Map.of("LATITUDE", Map.of("AS", "LATITUDE")),
                        Map.of("LONGITUDE", Map.of("AS", "LONGITUDE")),
                        Map.of("COUNTRY_REGION", Map.of("LIMIT", 1, "AS", "COUNTRY_REGION")),
                        Map.of("COUNTRY_REGION_TYPE", Map.of("LIMIT", 1, "AS", "COUNTRY_REGION_TYPE")),
                        Map.of("NUTS1", Map.of("AS", "NUTS1")),
                        Map.of("WORLD_REGION", Map.of("AS", "WORLD_REGION")),
                        Map.of("US_STATE", Map.of("AS", "US_STATE")),
                        Map.of("COUNTY", Map.of("AS", "COUNTY")),
                        Map.of("ADDRESS_TYPE", Map.of("AS", "ADDRESS_TYPE")),
                        Map.of("PHONE_NUMBER", Map.of("LIMIT", 1, "AS", "PHONE_NUMBER")),
                        Map.of("WEBSITE", Map.of("LIMIT", 1, "AS", "WEBSITE")),
                        Map.of("EMAIL", Map.of("LIMIT", 1, "AS", "EMAIL")),
                        Map.of("TRADE_DESCRIPTION_EN", Map.of("AS", "TRADE_DESCRIPTION_EN")),
                        Map.of("PRODUCTS_SERVICES", Map.of("AS", "PRODUCTS_SERVICES")),
                        Map.of("INDUSTRY_PRIMARY_CODE", Map.of("LIMIT", 1, "AS", "INDUSTRY_PRIMARY_CODE")),
                        Map.of("NACE2_MAIN_SECTION", Map.of("AS", "NACE2_MAIN_SECTION")),
                        Map.of("NACE2_CORE_CODE", Map.of("AS", "NACE2_CORE_CODE")),
                        Map.of("NACE2_PRIMARY_CODE", Map.of("LIMIT", 1, "AS", "NACE2_PRIMARY_CODE")),
                        Map.of("NAICS2022_CORE_CODE", Map.of("AS", "NAICS2022_CORE_CODE")),
                        Map.of("NAICS2022_CORE_LABEL", Map.of("AS", "NAICS2022_CORE_LABEL")),
                        Map.of("USSIC_PRIMARY_CODE", Map.of("LIMIT", 1, "AS", "USSIC_PRIMARY_CODE")),
                        Map.of("USSIC_PRIMARY_LABEL", Map.of("LIMIT", 1, "AS", "USSIC_PRIMARY_LABEL")),
                        Map.of("OPGSIZE", Map.of("AS", "OPGSIZE")),
                        Map.of("OVERVIEW_PRIMARY_BUSINESS_LINE", Map.of("AS", "OVERVIEW_PRIMARY_BUSINESS_LINE")),
                        Map.of("LEI_STATUS", Map.of("AS", "LEI_STATUS")),
                        Map.of("LEI", Map.of("AS", "LEI")),
                        Map.of("NATIONAL_ID", Map.of("LIMIT", 1, "AS", "NATIONAL_ID")),
                        Map.of("NATIONAL_ID_LABEL", Map.of("LIMIT", 1, "AS", "NATIONAL_ID_LABEL")),
                        Map.of("NATIONAL_ID_TYPE", Map.of("LIMIT", 1, "AS", "NATIONAL_ID_TYPE")),
                        Map.of("VAT_NUMBER", Map.of("LIMIT", 1, "AS", "VAT_NUMBER")),
                        Map.of("TRADE_REGISTER_NUMBER", Map.of("LIMIT", 1, "AS", "TRADE_REGISTER_NUMBER")),
                        Map.of("TIN", Map.of("AS", "TIN")),
                        Map.of("SWIFT_CODE", Map.of("AS", "SWIFT_CODE")),
                        Map.of("ISIN", Map.of("AS", "ISIN")),
                        Map.of("GRID_MATCH_INDICATOR", Map.of("AS", "GRID_MATCH_INDICATOR")),
                        Map.of("GRID_MATCH_SANCTIONS_INDICATOR", Map.of("AS", "GRID_MATCH_SANCTIONS_INDICATOR")),
                        Map.of("GRID_MATCH_WATCHLIST_INDICATOR", Map.of("AS", "GRID_MATCH_WATCHLIST_INDICATOR")),
                        Map.of("GRID_MATCH_PEP_INDICATOR", Map.of("AS", "GRID_MATCH_PEP_INDICATOR")),
                        Map.of("GRID_MATCH_MEDIA_INDICATOR", Map.of("AS", "GRID_MATCH_MEDIA_INDICATOR"))
                ))
                .build();
    }
}
