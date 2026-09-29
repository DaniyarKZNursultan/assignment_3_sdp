package com.infrastructure.legacy;

public class LegacyEnterprisePowerSupply {
    private final String powerBankId;
    private final double wattageKw;

    public LegacyEnterprisePowerSupply(String powerBankId) {
        this.powerBankId = powerBankId;
        this.wattageKw = 1200.0; // High-voltage 3-phase utility feed
    }

    public double getWattageKw() { return wattageKw; }
    public String getPowerBankId() { return powerBankId; }
}
