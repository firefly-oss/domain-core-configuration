package com.firefly.domain.configuration.core.config.reference;

/**
 * Enumerates every master-data entity that {@code domain-core-configuration} orchestrates
 * through the abstract reference-data service pattern.
 */
public enum ReferenceDataEntityType {

    COUNTRY,
    CURRENCY,
    BANK,
    LEGAL_FORM,
    TITLE,
    LANGUAGE_LOCALE,
    ADMINISTRATIVE_DIVISION,
    IDENTITY_DOCUMENT,
    IDENTITY_DOCUMENT_CATEGORY,
    CONSENT,
    RELATIONSHIP_TYPE,
    LOOKUP_DOMAIN,
    LOOKUP_ITEM,
    RULE_OPERATION_TYPE,
    TRANSACTION_CATEGORY,
    ASSET_TYPE,
    ACTIVITY_CODE,
    CONTRACT_TYPE,
    CONTRACT_ROLE,
    CONTRACT_ROLE_SCOPE,
    CONTRACT_DOCUMENT_TYPE,
    DOCUMENT_TEMPLATE,
    DOCUMENT_TEMPLATE_TYPE,
    NOTIFICATION_MESSAGE,
    NOTIFICATION_MESSAGE_TEMPLATE,
    MESSAGE_TYPE;

    /**
     * Kebab-case representation used by the saga layer and orchestration controller
     * to map URL path segments to the registered service.
     */
    public String kebabCaseName() {
        return name().toLowerCase().replace('_', '-');
    }

    /**
     * Inverse of {@link #kebabCaseName()} — parses a kebab-case token coming from
     * URL path or JSON payload into the matching enum constant.
     *
     * @throws IllegalArgumentException when the token matches no constant
     */
    public static ReferenceDataEntityType fromKebabCase(String kebab) {
        return valueOf(kebab.toUpperCase().replace('-', '_'));
    }
}
