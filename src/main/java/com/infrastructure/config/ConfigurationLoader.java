package com.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads infrastructure configuration dynamically from classpath properties,
 * system properties, or environment variables.
 */
public class ConfigurationLoader {

    public static final String DEFAULT_CONFIG_FILE = "config.properties";
    public static final String PROPERTY_FAMILY = "infrastructure.family";
    public static final String PROPERTY_ZONE_ID = "infrastructure.zoneId";
    public static final String ENV_FAMILY = "INFRASTRUCTURE_FAMILY";

    /**
     * Loads configuration resolving with precedence:
     * 1. System Property (-Dinfrastructure.family=...)
     * 2. Environment Variable (INFRASTRUCTURE_FAMILY=...)
     * 3. Specified properties resource file on classpath
     */
    public static String loadConfiguredFamily(String resourceFile) {
        // Check System Property
        String sysProp = System.getProperty(PROPERTY_FAMILY);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return sysProp.trim().toUpperCase();
        }

        // Check Environment Variable
        String envVar = System.getenv(ENV_FAMILY);
        if (envVar != null && !envVar.trim().isEmpty()) {
            return envVar.trim().toUpperCase();
        }

        // Load from Properties file
        Properties properties = loadProperties(resourceFile);
        String familyValue = properties.getProperty(PROPERTY_FAMILY);
        if (familyValue == null || familyValue.trim().isEmpty()) {
            throw new ConfigurationException("Property '" + PROPERTY_FAMILY + "' is missing or empty in " + resourceFile);
        }
        return familyValue.trim().toUpperCase();
    }

    public static Properties loadProperties(String resourceFile) {
        String path = (resourceFile != null && !resourceFile.trim().isEmpty()) ? resourceFile : DEFAULT_CONFIG_FILE;
        Properties properties = new Properties();

        try (InputStream is = ConfigurationLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new ConfigurationException("Configuration resource file not found on classpath: " + path);
            }
            properties.load(is);
            return properties;
        } catch (IOException e) {
            throw new ConfigurationException("Failed to read configuration resource: " + path, e);
        }
    }
}
