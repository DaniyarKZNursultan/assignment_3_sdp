package com.infrastructure.legacy;

/**
 * =========================================================================================
 * PART A: SYSTEM IMPLEMENTATION WITHOUT FACTORIES (LEGACY ANTI-PATTERN DEMONSTRATION)
 * =========================================================================================
 *
 * This client instantiates concrete infrastructure products directly using the 'new' keyword
 * and branching logic (if-else / switch) based on a runtime platform string.
 *
 * -----------------------------------------------------------------------------------------
 * DESIGN FLAW ANALYSIS (3 Specific Problems Identified):
 * -----------------------------------------------------------------------------------------
 * 1. TIGHT COUPLING TO CONCRETE IMPLEMENTATIONS:
 *    The client class has direct compile-time dependencies on every concrete class in the
 *    domain (LegacyEnterpriseCoolingUnit, LegacyEdgeCoolingUnit, LegacyExtremeCoolingUnit, etc.).
 *    This violates the Dependency Inversion Principle (DIP). If any constructor signature changes
 *    or a concrete implementation is swapped, this client code breaks and must be recompiled.
 *
 * 2. CODE DUPLICATION & VIOLATION OF OPEN/CLOSED PRINCIPLE (OCP):
 *    Every time a new environment/family is added (e.g., Space Station or Subsea), developers
 *    must modify every single 'if-else' or 'switch' statement throughout the codebase. The
 *    instantiation logic is duplicated across multiple setup methods (deployCooling, deployPower,
 *    deployMonitoring), increasing maintenance overhead and the risk of regressions.
 *
 * 3. CRITICAL RISK OF MIXING INCOMPATIBLE PRODUCT FAMILIES (CROSS-FAMILY CONTAMINATION):
 *    Because the client manually manages raw object creation without structural or compile-time
 *    family constraints, nothing prevents a programmer from accidentally wiring an
 *    Enterprise Chilled-Water CRAH unit with an Edge 48V DC Micro-Battery or an Extreme Arctic
 *    Sensor (e.g., see method demonstrateAccidentalCrossFamilyMixing()). In a real critical
 *    infrastructure system, this leads to catastrophic hardware failure or runtime crashes.
 * =========================================================================================
 */
public class LegacySystemClient {

    private final String environmentPlatform;

    // Direct references to concrete products (tight coupling)
    private LegacyEnterpriseCoolingUnit enterpriseCooling;
    private LegacyEnterprisePowerSupply enterprisePower;
    private LegacyEnterpriseSensor enterpriseSensor;

    private LegacyEdgeCoolingUnit edgeCooling;
    private LegacyEdgePowerSupply edgePower;
    private LegacyEdgeSensor edgeSensor;

    private LegacyExtremeCoolingUnit extremeCooling;
    private LegacyExtremePowerSupply extremePower;
    private LegacyExtremeSensor extremeSensor;

    public LegacySystemClient(String environmentPlatform) {
        this.environmentPlatform = environmentPlatform;
    }

    /**
     * Anti-pattern: Monolithic conditional creation method.
     * Every new family requires opening this method and modifying existing code.
     */
    public void initializeInfrastructure(String zoneId) {
        // DESIGN FLAW #2: Large, brittle branching logic duplicated for each component
        if ("ENTERPRISE".equalsIgnoreCase(environmentPlatform)) {
            // Direct use of 'new' operator (DESIGN FLAW #1: Tight coupling)
            this.enterpriseCooling = new LegacyEnterpriseCoolingUnit("CW-LOOP-" + zoneId);
            this.enterprisePower = new LegacyEnterprisePowerSupply("PWR-MAIN-" + zoneId);
            this.enterpriseSensor = new LegacyEnterpriseSensor("SNMP-RACK-" + zoneId);
            System.out.println("[LegacyClient] Initialized Enterprise Data Center infrastructure directly.");
        } else if ("EDGE".equalsIgnoreCase(environmentPlatform)) {
            this.edgeCooling = new LegacyEdgeCoolingUnit("DX-FAN-" + zoneId);
            this.edgePower = new LegacyEdgePowerSupply("DC-MOD-" + zoneId);
            this.edgeSensor = new LegacyEdgeSensor("PROBE-IOT-" + zoneId);
            System.out.println("[LegacyClient] Initialized Edge Computing Node infrastructure directly.");
        } else if ("EXTREME".equalsIgnoreCase(environmentPlatform)) {
            this.extremeCooling = new LegacyExtremeCoolingUnit("CRYO-BATH-" + zoneId);
            this.extremePower = new LegacyExtremePowerSupply("RTG-ISO-" + zoneId);
            this.extremeSensor = new LegacyExtremeSensor("RAD-HARD-" + zoneId);
            System.out.println("[LegacyClient] Initialized Extreme Environment Station infrastructure directly.");
        } else {
            throw new IllegalArgumentException("Unknown or unsupported legacy platform: " + environmentPlatform);
        }
    }

    /**
     * Executes legacy operation. Notice how the client must check the platform again
     * to know which concrete instance to invoke.
     */
    public void runLegacyDiagnosticCycle() {
        if ("ENTERPRISE".equalsIgnoreCase(environmentPlatform) && enterpriseCooling != null) {
            System.out.println(enterpriseSensor.readSnmpTelemetry());
            enterpriseCooling.directCoolEnterprisePower(enterprisePower);
        } else if ("EDGE".equalsIgnoreCase(environmentPlatform) && edgeCooling != null) {
            System.out.println(edgeSensor.readLowLatencyEdgeTelemetry());
            edgeCooling.directCoolEdgePower(edgePower);
        } else if ("EXTREME".equalsIgnoreCase(environmentPlatform) && extremeCooling != null) {
            System.out.println(extremeSensor.readHardenedTelemetry());
            extremeCooling.directCoolExtremePower(extremePower);
        }
    }

    /**
     * DESIGN FLAW #3 DEMONSTRATION: Accidental Cross-Family Mixing.
     * The compiler permits instantiating mismatched parts because there are no family abstractions
     * or structural type constraints.
     */
    public static void demonstrateAccidentalCrossFamilyMixing() {
        System.out.println("--- DEMONSTRATING LEGACY FLAW: Incompatible Product Combination ---");
        // Programmer mistakenly instantiates an Enterprise cooling system (needs massive 500kW loop)
        LegacyEnterpriseCoolingUnit enterpriseChiller = new LegacyEnterpriseCoolingUnit("CHILLER-ALPHA");
        // But mistakenly combines it with an Edge micro power supply (only 15kW DC battery)
        LegacyEdgePowerSupply edgeBattery = new LegacyEdgePowerSupply("EDGE-BATTERY-99");

        // The legacy system has no generic or architectural safeguard!
        // At compile time, if methods accept Object or raw parameters, or if a developer wires them together:
        System.out.println("WARNING: Enterprise Chiller (500 kW capacity) misconfigured with Edge Battery ("
                + edgeBattery.getWattageKw() + " kW). Result: Overcooling, condensation damage, and system failure!");
    }

    public LegacyEnterpriseCoolingUnit getEnterpriseCooling() { return enterpriseCooling; }
    public LegacyEdgeCoolingUnit getEdgeCooling() { return edgeCooling; }
    public LegacyExtremeCoolingUnit getExtremeCooling() { return extremeCooling; }
}
