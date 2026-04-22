package com.firefly.domain.configuration.core.config.reference.exception;

/**
 * Raised when the master-data core returns HTTP 409 — typically a unique-key or FK violation.
 */
public class ReferenceDataConflictException extends RuntimeException {

    public ReferenceDataConflictException(String message) {
        super(message);
    }

    public ReferenceDataConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
