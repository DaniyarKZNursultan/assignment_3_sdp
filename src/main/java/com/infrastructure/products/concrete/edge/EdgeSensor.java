package com.infrastructure.products.concrete.edge;

import com.infrastructure.family.EdgeFamily;
import com.infrastructure.model.SensorStatus;
import com.infrastructure.model.TelemetryReading;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;

/**
 * Concrete Product: Edge Computing Low-Latency IoT Telemetry Probe.
 */
public class EdgeSensor implements MonitoringSensor<EdgeFamily> {

    private final String probeId;
    private double currentTempCelsius;
    private double vibrationG;
    private double latencyMs;
    private double calibrationTolerance;
    private SensorStatus status;
    private int auditCycleCounter;

    public EdgeSensor(String probeId, double initialCalibrationTolerance) {
        this.probeId = probeId;
        this.currentTempCelsius = 36.5;
        this.vibrationG = 0.02;
        this.latencyMs = 1.2;
        this.calibrationTolerance = initialCalibrationTolerance;
        this.status = SensorStatus.ONLINE_NOMINAL;
        this.auditCycleCounter = 0;
    }

    @Override
    public EdgeFamily getFamily() {
        return EdgeFamily.INSTANCE;
    }

    @Override
    public String getSensorId() {
        return probeId;
    }

    @Override
    public SensorStatus getStatus() {
        return status;
    }

    @Override
    public TelemetryReading readTelemetry() {
        return TelemetryReading.of(
                probeId,
                currentTempCelsius,
                14.0,
                8.5,
                vibrationG,
                "Chassis Vibration (G)",
                status,
                "Edge Micro-Node Telemetry (Latency: " + latencyMs + "ms)"
        );
    }

    @Override
    public TelemetryReading readTelemetry(PowerSupply<EdgeFamily> powerSupply) {
        // Enforced at compile-time: can only read from EdgePowerSupply
        double dissipation = powerSupply.calculateHeatDissipationKw();
        this.currentTempCelsius = 35.0 + (dissipation * 2.2);

        if (currentTempCelsius > 55.0 || vibrationG > 1.5) {
            this.status = SensorStatus.CRITICAL_BREACH;
        } else if (currentTempCelsius > 45.0) {
            this.status = SensorStatus.WARNING_ELEVATED;
        } else {
            this.status = SensorStatus.ONLINE_NOMINAL;
        }

        return TelemetryReading.of(
                probeId,
                currentTempCelsius,
                powerSupply.getSuppliedWattageKw(),
                dissipation,
                vibrationG,
                "Chassis Vibration (G)",
                status,
                "Edge Real-time Telemetry: Temp=" + currentTempCelsius + "C"
        );
    }

    @Override
    public void calibrate(double baselineTolerance) {
        this.status = SensorStatus.CALIBRATING;
        this.calibrationTolerance = baselineTolerance;
        this.latencyMs = 1.0 + (baselineTolerance * 0.5);
        this.status = SensorStatus.ONLINE_NOMINAL;
    }

    @Override
    public boolean isThresholdExceeded() {
        return currentTempCelsius > 48.0 || vibrationG > 0.8;
    }

    @Override
    public void triggerDiagnosticAudit() {
        this.auditCycleCounter++;
        this.latencyMs = 0.95; // Optimal edge bus latency achieved
        this.status = SensorStatus.ONLINE_NOMINAL;
    }

    public void simulateThermalSurge(double tempDelta) {
        this.currentTempCelsius += tempDelta;
        if (this.currentTempCelsius > 55.0) {
            this.status = SensorStatus.CRITICAL_BREACH;
        } else if (this.currentTempCelsius > 45.0) {
            this.status = SensorStatus.WARNING_ELEVATED;
        }
    }

    public double getCurrentTempCelsius() { return currentTempCelsius; }
    public double getVibrationG() { return vibrationG; }
    public double getLatencyMs() { return latencyMs; }
    public int getAuditCycleCounter() { return auditCycleCounter; }
}
