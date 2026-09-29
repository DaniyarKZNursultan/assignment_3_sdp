package com.infrastructure.products.concrete.extreme;

import com.infrastructure.family.ExtremeFamily;
import com.infrastructure.model.SensorStatus;
import com.infrastructure.model.TelemetryReading;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;

/**
 * Concrete Product: Extreme Environment Sapphire-Window Optical & Pressure Hardened Sensor.
 */
public class ExtremeSensor implements MonitoringSensor<ExtremeFamily> {

    private final String hardenedProbeId;
    private double currentTempCelsius;
    private double hydrostaticPressureBar;
    private double radiationDoseRateMSv;
    private double calibrationTolerance;
    private SensorStatus status;
    private int auditCycleCounter;

    public ExtremeSensor(String hardenedProbeId, double initialCalibrationTolerance) {
        this.hardenedProbeId = hardenedProbeId;
        this.currentTempCelsius = -35.0;
        this.hydrostaticPressureBar = 140.0;
        this.radiationDoseRateMSv = 0.04;
        this.calibrationTolerance = initialCalibrationTolerance;
        this.status = SensorStatus.ONLINE_NOMINAL;
        this.auditCycleCounter = 0;
    }

    @Override
    public ExtremeFamily getFamily() {
        return ExtremeFamily.INSTANCE;
    }

    @Override
    public String getSensorId() {
        return hardenedProbeId;
    }

    @Override
    public SensorStatus getStatus() {
        return status;
    }

    @Override
    public TelemetryReading readTelemetry() {
        return TelemetryReading.of(
                hardenedProbeId,
                currentTempCelsius,
                450.0,
                280.0,
                hydrostaticPressureBar,
                "Hydrostatic Pressure (Bar)",
                status,
                "Extreme Subterranean/Arctic Telemetry (Radiation: " + radiationDoseRateMSv + " mSv/h)"
        );
    }

    @Override
    public TelemetryReading readTelemetry(PowerSupply<ExtremeFamily> powerSupply) {
        // Enforced at compile-time: can only read from ExtremePowerSupply
        double dissipation = powerSupply.calculateHeatDissipationKw();
        this.currentTempCelsius = -35.0 + (dissipation * 0.4);

        if (currentTempCelsius > 15.0 || hydrostaticPressureBar > 220.0 || radiationDoseRateMSv > 1.0) {
            this.status = SensorStatus.CRITICAL_BREACH;
        } else if (currentTempCelsius > -10.0 || hydrostaticPressureBar > 180.0) {
            this.status = SensorStatus.WARNING_ELEVATED;
        } else {
            this.status = SensorStatus.ONLINE_NOMINAL;
        }

        return TelemetryReading.of(
                hardenedProbeId,
                currentTempCelsius,
                powerSupply.getSuppliedWattageKw(),
                dissipation,
                hydrostaticPressureBar,
                "Hydrostatic Pressure (Bar)",
                status,
                "Extreme Subsea Telemetry: CoreTemp=" + currentTempCelsius + "C"
        );
    }

    @Override
    public void calibrate(double baselineTolerance) {
        this.status = SensorStatus.CALIBRATING;
        this.calibrationTolerance = baselineTolerance;
        this.hydrostaticPressureBar = 140.0 + (baselineTolerance * 2.0);
        this.status = SensorStatus.ONLINE_NOMINAL;
    }

    @Override
    public boolean isThresholdExceeded() {
        return currentTempCelsius > 5.0 || hydrostaticPressureBar > 190.0;
    }

    @Override
    public void triggerDiagnosticAudit() {
        this.auditCycleCounter++;
        this.radiationDoseRateMSv = Math.max(0.01, this.radiationDoseRateMSv - 0.005);
        this.status = SensorStatus.ONLINE_NOMINAL;
    }

    public void simulateThermalSurge(double tempDelta) {
        this.currentTempCelsius += tempDelta;
        if (this.currentTempCelsius > 15.0) {
            this.status = SensorStatus.CRITICAL_BREACH;
        } else if (this.currentTempCelsius > -10.0) {
            this.status = SensorStatus.WARNING_ELEVATED;
        }
    }

    public double getCurrentTempCelsius() { return currentTempCelsius; }
    public double getHydrostaticPressureBar() { return hydrostaticPressureBar; }
    public double getRadiationDoseRateMSv() { return radiationDoseRateMSv; }
    public int getAuditCycleCounter() { return auditCycleCounter; }
}
