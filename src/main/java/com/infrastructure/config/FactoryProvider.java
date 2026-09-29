package com.infrastructure.config;

import com.infrastructure.abstractfactory.EdgeInfrastructureFactory;
import com.infrastructure.abstractfactory.EnterpriseInfrastructureFactory;
import com.infrastructure.abstractfactory.ExtremeInfrastructureFactory;
import com.infrastructure.abstractfactory.InfrastructureFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * =========================================================================================
 * PART E: RUNTIME FACTORY SELECTION (FACTORY REGISTRY & RESOLVER)
 * =========================================================================================
 *
 * This provider decouples the client application from concrete factory instantiation.
 * The client never calls 'new EnterpriseInfrastructureFactory()'.
 * Instead, the application passes configuration (file, environment variable, CLI argument),
 * and FactoryProvider dynamically instantiates the correct factory.
 *
 * EXTENSIBILITY / OPEN-CLOSED PRINCIPLE (OCP):
 * New factories can be registered dynamically via {@link #registerFactory(String, Supplier)}
 * without modifying the existing resolution logic.
 * =========================================================================================
 */
public class FactoryProvider {

    private static final Map<String, Supplier<InfrastructureFactory<?>>> REGISTRY = new ConcurrentHashMap<>();

    static {
        // Register initial core product families
        registerFactory(EnterpriseInfrastructureFactory.IDENTIFIER, EnterpriseInfrastructureFactory::new);
        registerFactory(EdgeInfrastructureFactory.IDENTIFIER, EdgeInfrastructureFactory::new);
        registerFactory(ExtremeInfrastructureFactory.IDENTIFIER, ExtremeInfrastructureFactory::new);
    }

    private FactoryProvider() {}

    /**
     * Registers a new infrastructure factory supplier into the runtime registry.
     * Supports dynamic runtime extension (Open/Closed Principle).
     */
    public static void registerFactory(String familyIdentifier, Supplier<InfrastructureFactory<?>> factorySupplier) {
        if (familyIdentifier == null || familyIdentifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Family identifier cannot be null or empty.");
        }
        if (factorySupplier == null) {
            throw new IllegalArgumentException("Factory supplier cannot be null.");
        }
        REGISTRY.put(familyIdentifier.trim().toUpperCase(), factorySupplier);
    }

    /**
     * Obtains an InfrastructureFactory dynamically based on a family string identifier.
     *
     * @param familyIdentifier e.g. "ENTERPRISE", "EDGE", "EXTREME", "SPACE_STATION"
     * @return InfrastructureFactory instance for the requested family.
     * @throws UnknownFamilyException if the family is not registered.
     */
    public static InfrastructureFactory<?> getFactory(String familyIdentifier) {
        if (familyIdentifier == null || familyIdentifier.trim().isEmpty()) {
            throw new UnknownFamilyException("Requested family identifier cannot be null or empty.");
        }

        String normalizedKey = familyIdentifier.trim().toUpperCase();
        Supplier<InfrastructureFactory<?>> supplier = REGISTRY.get(normalizedKey);

        if (supplier == null) {
            throw new UnknownFamilyException("Unknown infrastructure family: '" + familyIdentifier +
                    "'. Registered available families: " + getAvailableFamilies());
        }

        return supplier.get();
    }

    /**
     * Obtains an InfrastructureFactory dynamically from the default config.properties file.
     */
    public static InfrastructureFactory<?> getFactoryFromConfig() {
        return getFactoryFromConfig(ConfigurationLoader.DEFAULT_CONFIG_FILE);
    }

    /**
     * Obtains an InfrastructureFactory dynamically from a specific configuration resource path.
     */
    public static InfrastructureFactory<?> getFactoryFromConfig(String resourcePath) {
        String familyCode = ConfigurationLoader.loadConfiguredFamily(resourcePath);
        return getFactory(familyCode);
    }

    /**
     * Returns the set of all registered family identifiers.
     */
    public static Set<String> getAvailableFamilies() {
        return Collections.unmodifiableSet(REGISTRY.keySet());
    }

    /**
     * Helper for testing: checks if a family identifier is registered.
     */
    public static boolean isFamilyRegistered(String familyIdentifier) {
        if (familyIdentifier == null) return false;
        return REGISTRY.containsKey(familyIdentifier.trim().toUpperCase());
    }
}
