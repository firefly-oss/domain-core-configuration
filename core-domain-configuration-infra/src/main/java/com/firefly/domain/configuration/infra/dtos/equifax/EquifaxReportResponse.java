package com.firefly.domain.configuration.infra.dtos.equifax;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquifaxReportResponse {

    private String status;
    private Consumers consumers;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<Links> links;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Consumers {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<EquifaxUSConsumerCreditReport> equifaxUSConsumerCreditReport;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<TwnSelectReport> twnSelectReport;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<DataxReport> dataxReport;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EquifaxUSConsumerCreditReport {
        private String identifier;
        private String customerReferenceNumber;
        private String customerNumber;
        private Integer consumerReferralCode;
        private Integer multipleReportIndicator;
        @JsonProperty("ECOAInquiryType")
        private String ecoaInquiryType;
        private HitCode hitCode;
        private String fileSinceDate;
        private String lastActivityDate;
        private String reportDate;
        private SubjectName subjectName;
        private Long subjectSocialNum;
        private String birthDate;
        private NameMatchFlags nameMatchFlags;
        private String addressDiscrepancyIndicator;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<FraudAlertCode> fraudSocialNumAlertCode;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<FraudIDScanAlertCodes> fraudIDScanAlertCodes;
        private FraudVictimIndicator fraudVictimIndicator;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<Addresses> addresses;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<AlertContacts> alertContacts;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<Trades> trades;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<Inquiries> inquiries;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<Models> models;
        @JsonProperty("OFACAlerts")
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<OfacAlerts> ofacAlerts;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<ConsumerReferralLocation> consumerReferralLocation;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<AlternateDataSources> alternateDataSources;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HitCode {
        private Integer code;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectName {
        private String firstName;
        private String lastName;
        private String middleName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NameMatchFlags {
        private String firstNameMatchFlag;
        private String lastNameMatchFlag;
        private String middleNameMatchFlag;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FraudAlertCode {
        private String code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FraudIDScanAlertCodes {
        private String code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FraudVictimIndicator {
        private String code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Addresses {
        private String addressType;
        private Integer houseNumber;
        private String streetName;
        private String streetType;
        private String cityName;
        private String stateAbbreviation;
        private Integer zipCode;
        private SourceOfAddress sourceOfAddress;
        private String dateFirstReported;
        private String dateLastReported;
        private String addressLine1;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SourceOfAddress {
        private String code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertContacts {
        private AlertType alertType;
        private String dateReported;
        private String effectiveDate;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<TelephoneNumbers> telephoneNumbers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertType {
        private String code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TelephoneNumbers {
        private TelephoneNumberType telephoneNumberType;
        private Long telephoneNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TelephoneNumberType {
        private String code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Trades {
        public String customerNumber;
        public String automatedUpdateIndicator;
        public Integer monthsReviewed;
        public AccountDesignator accountDesignator;
        public Integer accountNumber;
        public Integer thirtyDayCounter;
        public Integer sixtyDayCounter;
        public Integer ninetyDayCounter;
        public Integer previousHighRate1;
        public String previousHighDate1;
        public Integer previousHighRate2;
        public String previousHighDate2;
        public Integer previousHighRate3;
        public String previousHighDate3;
        @JsonProperty("24MonthPaymentHistory")
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public ArrayList<PaymentHistory> _24MonthPaymentHistory;
        public String customerName;
        public String dateReported;
        public String dateOpened;
        public Rate rate;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public ArrayList<NarrativeCodes> narrativeCodes;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public ArrayList<String> rawNarrativeCodes;
        public AccountTypeCode accountTypeCode;
        public String lastPaymentDate;
        public String dateMajorDelinquencyFirstReported;
        public TermsFrequencyCode termsFrequencyCode;
        public String code;
        public String description;
        public ActivityDesignatorCode activityDesignatorCode;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public ArrayList<PaymentHistory> paymentHistory1to24;
        public String lastActivityDate;
        public Integer highCredit;
        public PortfolioTypeCode portfolioTypeCode;
        public Integer actualPaymentAmount;
        public Integer scheduledPaymentAmount;
        public TermsDurationCode termsDurationCode;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PortfolioTypeCode{
        public String code;
        public String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TermsDurationCode{
        public String code;
        public String description;
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountDesignator {
        private String code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TermsFrequencyCode {
        private String code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentHistory {
        private String code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rate {
        private Integer code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NarrativeCodes {
        private String code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountTypeCode {
        private Integer code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityDesignatorCode {
        private String code;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Inquiries {
        private String type;
        private String industryCode;
        private String inquiryDate;
        private String customerNumber;
        private String customerName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Models {
        private String type;
        private String modelNumber;
        private Integer score;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<Reasons> reasons;
        private ScoreNumberOrMarketMaxIndustryCode scoreNumberOrMarketMaxIndustryCode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reasons {
        private Integer code;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreNumberOrMarketMaxIndustryCode {
        private Integer code;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfacAlerts {
        private Integer revisedLegalVerbiageIndicator;
        private String memberFirmCode;
        private String cdcTransactionDate;
        private Integer cdcTransactionTime;
        private String transactionType;
        private String cdcResponseCode;
        private String legalVerbiage;
        private String dataSegmentRegulated;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsumerReferralLocation {
        private Integer bureauCode;
        private String bureauName;
        private ReferralAddress address;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<ReferralTelephone> telephoneNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferralAddress {
        private String cityName;
        private String stateAbbreviation;
        private Long zipCode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferralTelephone {
        private Long telephoneNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlternateDataSources {
        private MilitaryLendingCoveredBorrower militaryLendingCoveredBorrower;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MilitaryLendingCoveredBorrower {
        private String regulatedIdentifier;
        private String disclaimer;
        private String coveredBorrowerStatus;
        private String referralContactNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TwnSelectReport {
        private String identifier;
        private TwnSelect twnSelect;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TwnSelect {
        private SignOn signOn;
        private String transactionId;
        private Integer statusCode;
        private String statusSeverity;
        private Long masterServerTransactionId;
        private String transactionPurposeCode;
        private String transactionPurposeMessage;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<TwnSelectResponses> twnSelectResponses;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignOn {
        private Integer statusCode;
        private String statusSeverity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TwnSelectResponses {
        private String dateOfTransaction;
        private BaseCompensation baseCompensation;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<AnnualCompensation> annualCompensation;
        private Employer employer;
        private Employee employee;
        private Object benefits;
        private String compensationAdjustmentLastDate;
        private Integer compensationAdjustmentLastAmountIncrease;
        private String compensationAdjustmentNextDate;
        private Integer compensationAdjustmentNextAmountIncrease;
        private String completenessOfTheData;
        private Long serverAssignedId;
        private List<Object> isDemoTransaction;
        private Integer projectedIncome;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseCompensation {
        private PayFrequency payFrequency;
        private Integer rateOfPayPerPayPeriod;
        private Integer averageHoursWorkedPerPayPeriod;
        private PayFrequency payPeriodFrequency;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayFrequency {
        private Integer code;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnualCompensation {
        private Integer yearCalendar;
        private Integer yearToDateBase;
        private Integer yearToDateOvertime;
        private Integer yearToDateCommission;
        private Integer yearToDateBonus;
        private Integer yearToDateOther;
        private Integer yearToDateTotal;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Employer {
        private Integer code;
        private String namePart1;
        private String addressLine1;
        private String city;
        private String stateOrProvince;
        private Integer postalCode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Employee {
        private Long ssn;
        private String firstName;
        private String middleName;
        private String lastName;
        private String positionTitle;
        private String divisionCode;
        private Integer statusDataCode;
        private String statusDataMessage;
        private String dateInformationEffective;
        private String dateOfTheMostRecentHire;
        private String originalHireDate;
        private Integer totalLengthOfServiceInMonths;
        private String dateEmploymentTerminated;
        private String reasonForTermination;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataxReport {
        private String identifier;
        private DataxCreditReportResponse dataxCreditReportResponse;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataxCreditReportResponse {
        private Transaction transaction;
        private CraInquirySegment craInquirySegment;
        private GlobalDecision globalDecision;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Transaction {
        private Long generationTime;
        private String codeVersion;
        private Integer requestVersion;
        private Long transactionId;
        private String trackHash;
        private String trackId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CraInquirySegment {
        private CraResponse craResponse;
        private CustomDecision customDecision;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CraResponse {
        private Integer version;
        @JsonProperty("transactionID")
        private Long transactionId;
        @JsonProperty("inquiryID")
        private Long inquiryId;
        private String inquiryDateTime;
        private DataxConsumer consumer;
        private DataxReportDetails report;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataxConsumer {
        private Long id;
        private DataxName name;
        private Long ssn;
        private String dob;
        private DataxLicense license;
        private DataxAddress address;
        private DataxEmployer employer;
        private Long phoneHome;
        private Long phoneCell;
        private Long phoneWork;
        private Integer phoneExt;
        private String email;
        private String ipAddress;
        private BankAccount bankAccount;
        private Integer requestedLoanAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataxName {
        private String first;
        private String middle;
        private String last;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataxLicense {
        private String state;
        private Long number;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataxAddress {
        private String name;
        private String street1;
        private String street2;
        private String city;
        private String state;
        private Integer zip;
        private String housingStatus;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataxEmployer {
        private String name;
        private String street1;
        private String street2;
        private String city;
        private String state;
        private Integer zip;
        private String payPeriod;
        private String nextPayDate;
        private Double monthlyIncome;
        private Boolean directDeposit;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankAccount {
        private String name;
        private Long account;
        private Long abanumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataxReportDetails {
        private ConsumerSummarySegment consumerSummarySegment;
        private InquirySegment inquirySegment;
        private TradelineDetailSegment tradelineDetailSegment;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsumerSummarySegment {
        private Indicators indicators;
        private UniqueIdentifiersSummary uniqueIdentifiersSummary;
        private ConsumerAlertsDisputesFreezes consumerAlertsDisputesFreezes;
        private AccountInfo account;
        private AllTransactionsSummary allTransactionsSummary;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Indicators {
        private IndicatorSummary indicatorSummary;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndicatorSummary {
        private Integer count;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<IndicatorDetail> indicator;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndicatorDetail {
        private Integer count;
        private String code;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UniqueIdentifiersSummary {
        private IdentifierSummaryDetail name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdentifierSummaryDetail {
        private Integer daysSince;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<TotalSummary> total;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalSummary {
        private Integer days;
        private Integer text;
        private Integer years;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsumerAlertsDisputesFreezes {
        private String activeDuty;
        private String activeDutyExp;
        private String initialFraud;
        private String extendedFraud;
        private String fraudExpiration;
        private String freeze;
        private String freezeDate;
        private String softInquiryFreeze;
        private String softInquiryFreezeExpiration;
        private String dispute;
        private String disputeContents;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountInfo {
        private String lastAccountTransactionStatus;
        private String lastAccountTransactionDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllTransactionsSummary {
        private VendorInquiries vendorInquiries;
        private Integer currentTradelines;
        private Integer currentTradelinesComputed;
        private Integer totalTradelines;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorInquiries {
        private Integer daysSince;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<TotalSummary> total;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InquirySegment {
        private Integer count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TradelineDetailSegment {
        private Integer count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomDecision {
        private String result;
        private String bucket;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GlobalDecision {
        private String result;
        private String craBucket;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Links {
        private String identifier;
        private String type;
        private String href;
    }
}
