package com.infrastructure.legacy;

public class LegacyEdgeSensor {
    private final String probeId;

    public LegacyEdgeSensor(String probeId) {
        this.probeId = probeId;
    }

    public String readLowLatencyEdgeTelemetry() {
        return "EDGE_TELEMETRY: Probe " + probeId + " temp=36.8C, vibration=low, signal=98%";
    }

    public String getProbeId() { return probeId; }
}
