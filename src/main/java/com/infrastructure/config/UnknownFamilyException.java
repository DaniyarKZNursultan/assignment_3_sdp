package com.infrastructure.config;

/**
 * Thrown when an unrecognized or unsupported product family identifier is requested.
 */
public class UnknownFamilyException extends RuntimeException {
    public UnknownFamilyException(String message) {
        super(message);
    }

    public UnknownFamilyException(String message, Throwable cause) {
        super(message, cause);
    }
}
