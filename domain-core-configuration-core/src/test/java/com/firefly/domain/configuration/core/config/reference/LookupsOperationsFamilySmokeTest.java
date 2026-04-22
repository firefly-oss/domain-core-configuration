package com.firefly.domain.configuration.core.config.reference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.ActivityCodesApi;
import com.firefly.common.reference.master.data.sdk.api.AssetTypeApi;
import com.firefly.common.reference.master.data.sdk.api.LookupDomainsApi;
import com.firefly.common.reference.master.data.sdk.api.LookupItemsApi;
import com.firefly.common.reference.master.data.sdk.api.RuleOperationTypeApi;
import com.firefly.common.reference.master.data.sdk.api.TransactionCategoryCatalogApi;
import com.firefly.common.reference.master.data.sdk.model.LookupItemDTO;
import com.firefly.domain.configuration.core.config.reference.activity.ActivityCodeConfigService;
import com.firefly.domain.configuration.core.config.reference.activity.ActivityCodeMapper;
import com.firefly.domain.configuration.core.config.reference.asset.AssetTypeConfigService;
import com.firefly.domain.configuration.core.config.reference.asset.AssetTypeMapper;
import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataConflictException;
import com.firefly.domain.configuration.core.config.reference.lookup.LookupDomainConfigService;
import com.firefly.domain.configuration.core.config.reference.lookup.LookupDomainMapper;
import com.firefly.domain.configuration.core.config.reference.lookup.LookupItemConfigService;
import com.firefly.domain.configuration.core.config.reference.lookup.LookupItemMapper;
import com.firefly.domain.configuration.core.config.reference.rule.RuleOperationTypeConfigService;
import com.firefly.domain.configuration.core.config.reference.rule.RuleOperationTypeMapper;
import com.firefly.domain.configuration.core.config.reference.transaction.TransactionCategoryConfigService;
import com.firefly.domain.configuration.core.config.reference.transaction.TransactionCategoryMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LookupsOperationsFamilySmokeTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void lookupDomain_reportsCorrectEntityType() {
        LookupDomainConfigService svc = new LookupDomainConfigService(
                mock(LookupDomainsApi.class), mock(LookupDomainMapper.class), objectMapper);
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.LOOKUP_DOMAIN);
    }

    @Test
    void lookupItem_reportsCorrectEntityType() {
        LookupItemConfigService svc = new LookupItemConfigService(
                mock(LookupItemsApi.class), mock(LookupItemMapper.class), objectMapper);
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.LOOKUP_ITEM);
    }

    @Test
    void lookupItem_sdk409_isMappedToConflictException() {
        // Verifies the 409 → ReferenceDataConflictException mapping for FK-bearing entities.
        LookupItemsApi api = mock(LookupItemsApi.class);
        LookupItemMapper mapper = mock(LookupItemMapper.class);
        LookupItemDTO sdkDto = new LookupItemDTO();
        when(mapper.toSdk(any())).thenReturn(sdkDto);
        when(api.createItem(any(), any())).thenReturn(Mono.error(
                WebClientResponseException.create(HttpStatus.CONFLICT.value(), "Conflict", null, null, null)));

        LookupItemConfigService svc = new LookupItemConfigService(api, mapper, objectMapper);

        StepVerifier.create(svc.create(new com.firefly.domain.configuration.interfaces.rest.reference.LookupItemDto()))
                .expectError(ReferenceDataConflictException.class)
                .verify();
    }

    @Test
    void ruleOperationType_reportsCorrectEntityType() {
        RuleOperationTypeConfigService svc = new RuleOperationTypeConfigService(
                mock(RuleOperationTypeApi.class), mock(RuleOperationTypeMapper.class));
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.RULE_OPERATION_TYPE);
    }

    @Test
    void transactionCategory_reportsCorrectEntityType() {
        TransactionCategoryConfigService svc = new TransactionCategoryConfigService(
                mock(TransactionCategoryCatalogApi.class), mock(TransactionCategoryMapper.class), objectMapper);
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.TRANSACTION_CATEGORY);
    }

    @Test
    void assetType_reportsCorrectEntityType() {
        AssetTypeConfigService svc = new AssetTypeConfigService(
                mock(AssetTypeApi.class), mock(AssetTypeMapper.class), objectMapper);
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.ASSET_TYPE);
    }

    @Test
    void activityCode_reportsCorrectEntityType() {
        ActivityCodeConfigService svc = new ActivityCodeConfigService(
                mock(ActivityCodesApi.class), mock(ActivityCodeMapper.class), objectMapper);
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.ACTIVITY_CODE);
    }

    @SuppressWarnings("unused")
    private static UUID anyUuid() { return UUID.randomUUID(); }
}
