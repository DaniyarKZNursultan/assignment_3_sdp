package com.infrastructure.config;

/**
 * Thrown when system configuration files cannot be loaded, are missing, or contain malformed properties.
 */
public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
