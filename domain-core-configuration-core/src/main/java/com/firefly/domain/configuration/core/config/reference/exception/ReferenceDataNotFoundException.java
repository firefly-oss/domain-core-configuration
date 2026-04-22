package com.firefly.domain.configuration.core.config.reference.exception;

/**
 * Raised when the master-data core returns HTTP 404 for a reference-data lookup.
 */
public class ReferenceDataNotFoundException extends RuntimeException {

    public ReferenceDataNotFoundException(String message) {
        super(message);
    }

    public ReferenceDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
