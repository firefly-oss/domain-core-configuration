package com.firefly.domain.configuration.core.config.reference.exception;

/**
 * Raised by the saga layer when the requested entity type is not registered
 * or when saga-level validation rejects the request before dispatching any SDK call.
 */
public class ReferenceDataValidationException extends RuntimeException {

    public ReferenceDataValidationException(String message) {
        super(message);
    }

    public ReferenceDataValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
