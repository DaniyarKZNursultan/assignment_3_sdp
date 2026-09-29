package com.infrastructure.legacy;

/**
 * Direct legacy implementations representing hardware components prior to factory patterns.
 * In the legacy system, concrete classes are instantiated directly using 'new' operators.
 */
public class LegacyEnterpriseCoolingUnit {
    private final String coolingLoopId;
    private double currentChillKw;

    public LegacyEnterpriseCoolingUnit(String coolingLoopId) {
        this.coolingLoopId = coolingLoopId;
        this.currentChillKw = 500.0; // High-capacity chilled water loop
    }

    public void directCoolEnterprisePower(LegacyEnterprisePowerSupply powerSupply) {
        System.out.println("[Legacy Enterprise Cooling] Chilled water loop " + coolingLoopId +
                " actively dissipating " + powerSupply.getWattageKw() + " kW.");
    }

    public double getCurrentChillKw() { return currentChillKw; }
    public String getCoolingLoopId() { return coolingLoopId; }
}
