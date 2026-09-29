package com.infrastructure.legacy;

public class LegacyExtremePowerSupply {
    private final String generatorId;
    private final double wattageKw;

    public LegacyExtremePowerSupply(String generatorId) {
        this.generatorId = generatorId;
        this.wattageKw = 850.0; // High-durability thermoelectric/isotope power
    }

    public double getWattageKw() { return wattageKw; }
    public String getGeneratorId() { return generatorId; }
}
