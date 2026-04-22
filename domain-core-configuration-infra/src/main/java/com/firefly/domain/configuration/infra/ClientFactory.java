package com.firefly.domain.configuration.infra;

import com.firefly.common.reference.master.data.sdk.api.ActivityCodesApi;
import com.firefly.common.reference.master.data.sdk.api.AdministrativeDivisionsApi;
import com.firefly.common.reference.master.data.sdk.api.AssetTypeApi;
import com.firefly.common.reference.master.data.sdk.api.BankInstitutionCodesApi;
import com.firefly.common.reference.master.data.sdk.api.ConsentCatalogApi;
import com.firefly.common.reference.master.data.sdk.api.ContractDocumentTypeApi;
import com.firefly.common.reference.master.data.sdk.api.ContractRoleApi;
import com.firefly.common.reference.master.data.sdk.api.ContractRoleScopeApi;
import com.firefly.common.reference.master.data.sdk.api.ContractTypeApi;
import com.firefly.common.reference.master.data.sdk.api.CountriesApi;
import com.firefly.common.reference.master.data.sdk.api.CurrenciesApi;
import com.firefly.common.reference.master.data.sdk.api.DocumentTemplateTypesApi;
import com.firefly.common.reference.master.data.sdk.api.DocumentTemplatesApi;
import com.firefly.common.reference.master.data.sdk.api.IdentityDocumentCategoriesApi;
import com.firefly.common.reference.master.data.sdk.api.IdentityDocumentsApi;
import com.firefly.common.reference.master.data.sdk.api.LanguageLocaleApi;
import com.firefly.common.reference.master.data.sdk.api.LegalFormsApi;
import com.firefly.common.reference.master.data.sdk.api.LookupDomainsApi;
import com.firefly.common.reference.master.data.sdk.api.LookupItemsApi;
import com.firefly.common.reference.master.data.sdk.api.MessageTypeCatalogApi;
import com.firefly.common.reference.master.data.sdk.api.NotificationMessageCatalogApi;
import com.firefly.common.reference.master.data.sdk.api.NotificationMessageTemplatesApi;
import com.firefly.common.reference.master.data.sdk.api.RelationshipTypeMasterApi;
import com.firefly.common.reference.master.data.sdk.api.RuleOperationTypeApi;
import com.firefly.common.reference.master.data.sdk.api.TitleMasterApi;
import com.firefly.common.reference.master.data.sdk.api.TransactionCategoryCatalogApi;
import com.firefly.common.reference.master.data.sdk.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Default implementation of the ClientFactory interface.
 * Creates client service instances using the appropriate API clients and dependencies
 * for every master-data entity served by {@code core-common-reference-master-data}.
 */
@Component
public class ClientFactory {

    private final ApiClient apiClient;

    @Autowired
    public ClientFactory(MasterDataProperties masterDataProperties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(masterDataProperties.getBasePath());
    }

    @Bean
    public CountriesApi countriesApi() {
        return new CountriesApi(apiClient);
    }

    @Bean
    public CurrenciesApi currenciesApi() {
        return new CurrenciesApi(apiClient);
    }

    @Bean
    public LegalFormsApi legalFormsApi() {
        return new LegalFormsApi(apiClient);
    }

    @Bean
    public LanguageLocaleApi languageLocaleApi() {
        return new LanguageLocaleApi(apiClient);
    }

    @Bean
    public LookupDomainsApi lookupDomainsApi() {
        return new LookupDomainsApi(apiClient);
    }

    @Bean
    public BankInstitutionCodesApi bankInstitutionCodesApi() {
        return new BankInstitutionCodesApi(apiClient);
    }

    @Bean
    public TitleMasterApi titleMasterApi() {
        return new TitleMasterApi(apiClient);
    }

    @Bean
    public IdentityDocumentsApi identityDocumentsApi() {
        return new IdentityDocumentsApi(apiClient);
    }

    @Bean
    public IdentityDocumentCategoriesApi identityDocumentCategoriesApi() {
        return new IdentityDocumentCategoriesApi(apiClient);
    }

    @Bean
    public ActivityCodesApi activityCodesApi() {
        return new ActivityCodesApi(apiClient);
    }

    @Bean
    public TransactionCategoryCatalogApi transactionCategoryCatalogApi() {
        return new TransactionCategoryCatalogApi(apiClient);
    }

    @Bean
    public AssetTypeApi assetTypeApi() {
        return new AssetTypeApi(apiClient);
    }

    @Bean
    public ContractTypeApi contractTypeApi() {
        return new ContractTypeApi(apiClient);
    }

    @Bean
    public ContractRoleApi contractRoleApi() {
        return new ContractRoleApi(apiClient);
    }

    @Bean
    public ContractRoleScopeApi contractRoleScopeApi() {
        return new ContractRoleScopeApi(apiClient);
    }

    @Bean
    public ContractDocumentTypeApi contractDocumentTypeApi() {
        return new ContractDocumentTypeApi(apiClient);
    }

    @Bean
    public DocumentTemplatesApi documentTemplatesApi() {
        return new DocumentTemplatesApi(apiClient);
    }

    @Bean
    public DocumentTemplateTypesApi documentTemplateTypesApi() {
        return new DocumentTemplateTypesApi(apiClient);
    }

    @Bean
    public NotificationMessageCatalogApi notificationMessageCatalogApi() {
        return new NotificationMessageCatalogApi(apiClient);
    }

    @Bean
    public NotificationMessageTemplatesApi notificationMessageTemplatesApi() {
        return new NotificationMessageTemplatesApi(apiClient);
    }

    @Bean
    public MessageTypeCatalogApi messageTypeCatalogApi() {
        return new MessageTypeCatalogApi(apiClient);
    }

    @Bean
    public LookupItemsApi lookupItemsApi() {
        return new LookupItemsApi(apiClient);
    }

    @Bean
    public RelationshipTypeMasterApi relationshipTypeMasterApi() {
        return new RelationshipTypeMasterApi(apiClient);
    }

    @Bean
    public ConsentCatalogApi consentCatalogApi() {
        return new ConsentCatalogApi(apiClient);
    }

    @Bean
    public AdministrativeDivisionsApi administrativeDivisionsApi() {
        return new AdministrativeDivisionsApi(apiClient);
    }

    @Bean
    public RuleOperationTypeApi ruleOperationTypeApi() {
        return new RuleOperationTypeApi(apiClient);
    }

}
