package com.infrastructure.family;

/**
 * Marker interface defining the root contract for infrastructure product families.
 * Product types parameterize over {@code F extends EnvironmentFamily} to guarantee
 * structural, compile-time family safety.
 */
public interface EnvironmentFamily {
    String getFamilyCode();
    String getDescription();
}
