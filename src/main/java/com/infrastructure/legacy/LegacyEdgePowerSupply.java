package com.infrastructure.legacy;

public class LegacyEdgePowerSupply {
    private final String moduleSerial;
    private final double wattageKw;

    public LegacyEdgePowerSupply(String moduleSerial) {
        this.moduleSerial = moduleSerial;
        this.wattageKw = 15.0; // Compact 48V DC micro-battery/bus
    }

    public double getWattageKw() { return wattageKw; }
    public String getModuleSerial() { return moduleSerial; }
}
