package com.firefly.domain.configuration.web.controller.reference.base;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.*;

import java.util.EnumMap;
import java.util.Map;

/**
 * Maps each {@link ReferenceDataEntityType} to the concrete public DTO class
 * used by the admin REST surface. The orchestration controller relies on this
 * mapping to deserialize the opaque {@code payload} field of
 * {@code UpdateReferenceDataSagaRequest} into the typed domain DTO.
 */
public final class ReferenceDataTypeResolver {

    private static final Map<ReferenceDataEntityType, Class<?>> DTO_BY_TYPE = buildMap();

    private ReferenceDataTypeResolver() {
    }

    public static Class<?> dtoClassFor(ReferenceDataEntityType type) {
        Class<?> dto = DTO_BY_TYPE.get(type);
        if (dto == null) {
            throw new IllegalStateException("No DTO class registered for entity type " + type);
        }
        return dto;
    }

    private static Map<ReferenceDataEntityType, Class<?>> buildMap() {
        Map<ReferenceDataEntityType, Class<?>> map = new EnumMap<>(ReferenceDataEntityType.class);
        map.put(ReferenceDataEntityType.COUNTRY, CountryConfigDto.class);
        map.put(ReferenceDataEntityType.CURRENCY, CurrencyConfigDto.class);
        map.put(ReferenceDataEntityType.BANK, BankDto.class);
        map.put(ReferenceDataEntityType.LEGAL_FORM, LegalFormConfigDto.class);
        map.put(ReferenceDataEntityType.TITLE, TitleDto.class);
        map.put(ReferenceDataEntityType.LANGUAGE_LOCALE, LanguageLocaleDto.class);
        map.put(ReferenceDataEntityType.ADMINISTRATIVE_DIVISION, AdministrativeDivisionDto.class);
        map.put(ReferenceDataEntityType.IDENTITY_DOCUMENT, IdentityDocumentDto.class);
        map.put(ReferenceDataEntityType.IDENTITY_DOCUMENT_CATEGORY, IdentityDocumentCategoryDto.class);
        map.put(ReferenceDataEntityType.CONSENT, ConsentDto.class);
        map.put(ReferenceDataEntityType.RELATIONSHIP_TYPE, RelationshipTypeDto.class);
        map.put(ReferenceDataEntityType.LOOKUP_DOMAIN, LookupDomainDto.class);
        map.put(ReferenceDataEntityType.LOOKUP_ITEM, LookupItemDto.class);
        map.put(ReferenceDataEntityType.RULE_OPERATION_TYPE, RuleOperationTypeDto.class);
        map.put(ReferenceDataEntityType.TRANSACTION_CATEGORY, TransactionCategoryDto.class);
        map.put(ReferenceDataEntityType.ASSET_TYPE, AssetTypeDto.class);
        map.put(ReferenceDataEntityType.ACTIVITY_CODE, ActivityCodeDto.class);
        map.put(ReferenceDataEntityType.CONTRACT_TYPE, ContractTypeDto.class);
        map.put(ReferenceDataEntityType.CONTRACT_ROLE, ContractRoleDto.class);
        map.put(ReferenceDataEntityType.CONTRACT_ROLE_SCOPE, ContractRoleScopeDto.class);
        map.put(ReferenceDataEntityType.CONTRACT_DOCUMENT_TYPE, ContractDocumentTypeDto.class);
        map.put(ReferenceDataEntityType.DOCUMENT_TEMPLATE, DocumentTemplateDto.class);
        map.put(ReferenceDataEntityType.DOCUMENT_TEMPLATE_TYPE, DocumentTemplateTypeDto.class);
        map.put(ReferenceDataEntityType.NOTIFICATION_MESSAGE, NotificationMessageDto.class);
        map.put(ReferenceDataEntityType.NOTIFICATION_MESSAGE_TEMPLATE, NotificationMessageTemplateDto.class);
        map.put(ReferenceDataEntityType.MESSAGE_TYPE, MessageTypeDto.class);
        return map;
    }
}
