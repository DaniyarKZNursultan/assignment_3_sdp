package com.infrastructure.legacy;

public class LegacyEdgeCoolingUnit {
    private final String fanId;

    public LegacyEdgeCoolingUnit(String fanId) {
        this.fanId = fanId;
    }

    public void directCoolEdgePower(LegacyEdgePowerSupply powerSupply) {
        System.out.println("[Legacy Edge Cooling] Direct-expansion micro-fan " + fanId +
                " cooling compact DC unit drawing " + powerSupply.getWattageKw() + " kW.");
    }

    public String getFanId() { return fanId; }
}
