package com.infrastructure.products.concrete.space;

import com.infrastructure.family.SpaceStationFamily;
import com.infrastructure.model.SensorStatus;
import com.infrastructure.model.TelemetryReading;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;

/**
 * Concrete Product: Orbital Cosmic Radiation & Microgravity Environmental Sensor.
 */
public class SpaceStationSensor implements MonitoringSensor<SpaceStationFamily> {

    private final String telemetryModuleId;
    private double currentHullTempCelsius;
    private double cosmicRadiationFluxMev;
    private double microgravityDriftMps2;
    private double calibrationTolerance;
    private SensorStatus status;
    private int auditCycleCounter;

    public SpaceStationSensor(String telemetryModuleId, double initialCalibrationTolerance) {
        this.telemetryModuleId = telemetryModuleId;
        this.currentHullTempCelsius = -15.0;
        this.cosmicRadiationFluxMev = 420.0;
        this.microgravityDriftMps2 = 0.0001;
        this.calibrationTolerance = initialCalibrationTolerance;
        this.status = SensorStatus.ONLINE_NOMINAL;
        this.auditCycleCounter = 0;
    }

    @Override
    public SpaceStationFamily getFamily() {
        return SpaceStationFamily.INSTANCE;
    }

    @Override
    public String getSensorId() {
        return telemetryModuleId;
    }

    @Override
    public SensorStatus getStatus() {
        return status;
    }

    @Override
    public TelemetryReading readTelemetry() {
        return TelemetryReading.of(
                telemetryModuleId,
                currentHullTempCelsius,
                140.0,
                95.0,
                cosmicRadiationFluxMev,
                "Cosmic Radiation Flux (MeV)",
                status,
                "Orbital Telemetry (Drift: " + microgravityDriftMps2 + " m/s^2)"
        );
    }

    @Override
    public TelemetryReading readTelemetry(PowerSupply<SpaceStationFamily> powerSupply) {
        // Enforced at compile-time: can only read from SpaceStationPowerSupply
        double dissipation = powerSupply.calculateHeatDissipationKw();
        this.currentHullTempCelsius = -15.0 + (dissipation * 1.5);

        if (currentHullTempCelsius > 40.0 || cosmicRadiationFluxMev > 800.0) {
            this.status = SensorStatus.CRITICAL_BREACH;
        } else if (currentHullTempCelsius > 20.0 || cosmicRadiationFluxMev > 600.0) {
            this.status = SensorStatus.WARNING_ELEVATED;
        } else {
            this.status = SensorStatus.ONLINE_NOMINAL;
        }

        return TelemetryReading.of(
                telemetryModuleId,
                currentHullTempCelsius,
                powerSupply.getSuppliedWattageKw(),
                dissipation,
                cosmicRadiationFluxMev,
                "Cosmic Radiation Flux (MeV)",
                status,
                "Orbital Bus Telemetry: HullTemp=" + currentHullTempCelsius + "C"
        );
    }

    @Override
    public void calibrate(double baselineTolerance) {
        this.status = SensorStatus.CALIBRATING;
        this.calibrationTolerance = baselineTolerance;
        this.cosmicRadiationFluxMev = 400.0 + (baselineTolerance * 10.0);
        this.status = SensorStatus.ONLINE_NOMINAL;
    }

    @Override
    public boolean isThresholdExceeded() {
        return currentHullTempCelsius > 25.0 || cosmicRadiationFluxMev > 700.0;
    }

    @Override
    public void triggerDiagnosticAudit() {
        this.auditCycleCounter++;
        this.microgravityDriftMps2 = 0.00008; // Gyroscopic station keeping recalibrated
        this.status = SensorStatus.ONLINE_NOMINAL;
    }

    public void simulateSolarFlareSpike(double fluxDelta) {
        this.cosmicRadiationFluxMev += fluxDelta;
        if (this.cosmicRadiationFluxMev > 800.0) {
            this.status = SensorStatus.CRITICAL_BREACH;
        } else if (this.cosmicRadiationFluxMev > 600.0) {
            this.status = SensorStatus.WARNING_ELEVATED;
        }
    }

    public double getCurrentHullTempCelsius() { return currentHullTempCelsius; }
    public double getCosmicRadiationFluxMev() { return cosmicRadiationFluxMev; }
    public double getMicrogravityDriftMps2() { return microgravityDriftMps2; }
    public int getAuditCycleCounter() { return auditCycleCounter; }
}
