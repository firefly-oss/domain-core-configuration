package com.firefly.domain.configuration.core.utils.constants;

/**
 * Symbolic constants used by the cross-entity reference-data sagas.
 * Per-family CRUD operations bypass CQRS and therefore do not need command/event constants here.
 */
public final class ReferenceDataConstants {

    private ReferenceDataConstants() {
    }

    // Saga names
    public static final String SAGA_UPDATE_REFERENCE_DATA = "update-reference-data-saga";
    public static final String SAGA_ACTIVATE_REFERENCE_ENTITY = "activate-reference-entity-saga";

    // Saga-scoped command names
    public static final String CMD_UPDATE_REFERENCE_DATA = "update-reference-data";
    public static final String CMD_ACTIVATE_REFERENCE_ENTITY = "activate-reference-entity";
    public static final String CMD_CAPTURE_ENTITY_SNAPSHOT = "capture-entity-snapshot";
    public static final String CMD_REAPPLY_PREVIOUS_STATE = "reapply-previous-state";
    public static final String CMD_UPDATE_LOCALIZATION = "update-localization";

    // Saga step IDs
    public static final String STEP_CAPTURE_SNAPSHOT = "capture-snapshot";
    public static final String STEP_UPDATE_PRIMARY_ENTITY = "update-primary-entity";
    public static final String STEP_UPDATE_LOCALIZATIONS = "update-localizations";
    public static final String STEP_VALIDATE_CROSS_ENTITY = "validate-cross-entity";
    public static final String STEP_APPLY_STATUS_CHANGE = "apply-status-change";

    // Shared ExecutionContext variable keys
    public static final String CTX_PRIMARY_ENTITY_ID = "primaryEntityId";
    public static final String CTX_PREVIOUS_STATE = "previousState";
    public static final String CTX_PREVIOUS_LOCALIZATIONS = "previousLocalizations";
    public static final String CTX_CROSS_CHECK_PASSED = "crossCheckPassed";
    public static final String CTX_PREVIOUS_STATUS = "previousStatus";
    public static final String CTX_ENTITY_TYPE = "entityType";

    // Audit event types emitted by the sagas
    public static final String EVENT_REFERENCE_DATA_UPDATED = "reference-data.updated";
    public static final String EVENT_REFERENCE_ENTITY_ACTIVATED = "reference-data.activated";
    public static final String EVENT_REFERENCE_ENTITY_DEACTIVATED = "reference-data.deactivated";

    // Sentinels returned by no-op saga steps to keep Mono emissions non-empty
    public static final String SENTINEL_NO_LOCALIZATIONS = "no-localizations";
    public static final String SENTINEL_NO_CROSS_CHECK = "no-cross-check-needed";
    public static final String SENTINEL_CROSS_CHECK_PASSED = "cross-check-passed";
}
