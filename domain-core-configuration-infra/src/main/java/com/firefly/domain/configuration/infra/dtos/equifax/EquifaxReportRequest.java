package com.firefly.domain.configuration.infra.dtos.equifax;

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
public class EquifaxReportRequest {

    @JsonProperty("consumers")
    private Consumers consumers;

    @JsonProperty("customerReferenceidentifier")
    private String customerReferenceidentifier;

    @JsonProperty("customerConfiguration")
    private CustomerConfiguration customerConfiguration;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Consumers {
        @JsonProperty("name")
        private List<Name> name;
        @JsonProperty("socialNum")
        private List<SocialNum> socialNum;
        @JsonProperty("addresses")
        private List<Address> addresses;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Name {
        @JsonProperty("identifier")
        private String identifier;
        @JsonProperty("firstName")
        private String firstName;
        @JsonProperty("lastName")
        private String lastName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocialNum {
        @JsonProperty("identifier")
        private String identifier;
        @JsonProperty("number")
        private String number;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        @JsonProperty("identifier")
        private String identifier;
        @JsonProperty("houseNumber")
        private String houseNumber;
        @JsonProperty("streetName")
        private String streetName;
        @JsonProperty("streetType")
        private String streetType;
        @JsonProperty("city")
        private String city;
        @JsonProperty("state")
        private String state;
        @JsonProperty("zip")
        private String zip;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerConfiguration {
        @JsonProperty("equifaxUSConsumerCreditReport")
        private EquifaxUSConsumerCreditReport equifaxUSConsumerCreditReport;
        @JsonProperty("equifaxUSConsumerTwnRequest")
        private EquifaxUSConsumerTwnRequest equifaxUSConsumerTwnRequest;
        @JsonProperty("equifaxUSConsumerDataxInquiryRequest")
        private EquifaxUSConsumerDataxInquiryRequest equifaxUSConsumerDataxInquiryRequest;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EquifaxUSConsumerCreditReport {
        @JsonProperty("pdfComboIndicator")
        private String pdfComboIndicator;
        @JsonProperty("memberNumber")
        private String memberNumber;
        @JsonProperty("securityCode")
        private String securityCode;
        @JsonProperty("customerCode")
        private String customerCode;
        @JsonProperty("multipleReportIndicator")
        private String multipleReportIndicator;
        @JsonProperty("models")
        private List<Model> models;
        @JsonProperty("ECOAInquiryType")
        private String ecoaInquiryType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Model {
        @JsonProperty("identifier")
        private String identifier;
        @JsonProperty("modelField")
        private List<String> modelField;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EquifaxUSConsumerTwnRequest {
        @JsonProperty("userId")
        private String userId;
        @JsonProperty("userPassword")
        private String userPassword;
        @JsonProperty("permissiblePurposeCode")
        private String permissiblePurposeCode;
        @JsonProperty("templateName")
        private String templateName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EquifaxUSConsumerDataxInquiryRequest {
        @JsonProperty("authentication")
        private Authentication authentication;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Authentication {
        @JsonProperty("licensekey")
        private String licensekey;
        @JsonProperty("password")
        private String password;
    }
}
