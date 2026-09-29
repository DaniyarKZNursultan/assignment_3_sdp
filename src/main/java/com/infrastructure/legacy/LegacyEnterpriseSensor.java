package com.infrastructure.legacy;

public class LegacyEnterpriseSensor {
    private final String rackSensorId;

    public LegacyEnterpriseSensor(String rackSensorId) {
        this.rackSensorId = rackSensorId;
    }

    public String readSnmpTelemetry() {
        return "ENTERPRISE_TELEMETRY: Rack " + rackSensorId + " temp=22.4C, humidity=45%, airflow=normal";
    }

    public String getRackSensorId() { return rackSensorId; }
}
