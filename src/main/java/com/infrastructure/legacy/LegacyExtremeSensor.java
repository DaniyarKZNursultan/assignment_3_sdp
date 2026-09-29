package com.infrastructure.legacy;

public class LegacyExtremeSensor {
    private final String hardenedProbeId;

    public LegacyExtremeSensor(String hardenedProbeId) {
        this.hardenedProbeId = hardenedProbeId;
    }

    public String readHardenedTelemetry() {
        return "EXTREME_TELEMETRY: Hardened probe " + hardenedProbeId +
                " externalTemp=-55.0C, corePressure=120bar, radiation=0.04mSv";
    }

    public String getHardenedProbeId() { return hardenedProbeId; }
}
