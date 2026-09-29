package com.infrastructure.legacy;

public class LegacyExtremeCoolingUnit {
    private final String cryoChamberId;

    public LegacyExtremeCoolingUnit(String cryoChamberId) {
        this.cryoChamberId = cryoChamberId;
    }

    public void directCoolExtremePower(LegacyExtremePowerSupply powerSupply) {
        System.out.println("[Legacy Extreme Cooling] Cryo-dielectric bath " + cryoChamberId +
                " stabilizing subterranean power unit running at " + powerSupply.getWattageKw() + " kW.");
    }

    public String getCryoChamberId() { return cryoChamberId; }
}
