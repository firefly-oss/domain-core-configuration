package com.firefly.domain.configuration.core.config.reference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.ConsentCatalogApi;
import com.firefly.common.reference.master.data.sdk.api.IdentityDocumentCategoriesApi;
import com.firefly.common.reference.master.data.sdk.api.IdentityDocumentsApi;
import com.firefly.common.reference.master.data.sdk.api.RelationshipTypeMasterApi;
import com.firefly.domain.configuration.core.config.reference.consent.ConsentConfigService;
import com.firefly.domain.configuration.core.config.reference.consent.ConsentMapper;
import com.firefly.domain.configuration.core.config.reference.identity.IdentityDocumentCategoryConfigService;
import com.firefly.domain.configuration.core.config.reference.identity.IdentityDocumentCategoryMapper;
import com.firefly.domain.configuration.core.config.reference.identity.IdentityDocumentConfigService;
import com.firefly.domain.configuration.core.config.reference.identity.IdentityDocumentMapper;
import com.firefly.domain.configuration.core.config.reference.relationship.RelationshipTypeConfigService;
import com.firefly.domain.configuration.core.config.reference.relationship.RelationshipTypeMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class IdentityConsentRelationshipFamilySmokeTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void identityDocument_reportsCorrectEntityType() {
        IdentityDocumentConfigService svc = new IdentityDocumentConfigService(
                mock(IdentityDocumentsApi.class), mock(IdentityDocumentMapper.class), objectMapper);
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.IDENTITY_DOCUMENT);
    }

    @Test
    void identityDocumentCategory_reportsCorrectEntityType() {
        IdentityDocumentCategoryConfigService svc = new IdentityDocumentCategoryConfigService(
                mock(IdentityDocumentCategoriesApi.class), mock(IdentityDocumentCategoryMapper.class), objectMapper);
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.IDENTITY_DOCUMENT_CATEGORY);
    }

    @Test
    void consent_reportsCorrectEntityType() {
        ConsentConfigService svc = new ConsentConfigService(
                mock(ConsentCatalogApi.class), mock(ConsentMapper.class), objectMapper);
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.CONSENT);
    }

    @Test
    void relationshipType_reportsCorrectEntityType() {
        RelationshipTypeConfigService svc = new RelationshipTypeConfigService(
                mock(RelationshipTypeMasterApi.class), mock(RelationshipTypeMapper.class), objectMapper);
        assertThat(svc.entityType()).isEqualTo(ReferenceDataEntityType.RELATIONSHIP_TYPE);
    }
}
