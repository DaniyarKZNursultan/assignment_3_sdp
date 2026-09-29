package com.infrastructure.products.concrete.enterprise;

import com.infrastructure.family.EnterpriseFamily;
import com.infrastructure.model.SensorStatus;
import com.infrastructure.model.TelemetryReading;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;

/**
 * Concrete Product: Enterprise Data Center SNMP Rack Telemetry Array.
 */
public class EnterpriseSensor implements MonitoringSensor<EnterpriseFamily> {

    private final String sensorId;
    private double currentTempCelsius;
    private double humidityPercent;
    private double airflowCfm;
    private double calibrationTolerance;
    private SensorStatus status;
    private int auditCycleCounter;

    public EnterpriseSensor(String sensorId, double initialCalibrationTolerance) {
        this.sensorId = sensorId;
        this.currentTempCelsius = 22.4;
        this.humidityPercent = 45.0;
        this.airflowCfm = 3200.0;
        this.calibrationTolerance = initialCalibrationTolerance;
        this.status = SensorStatus.ONLINE_NOMINAL;
        this.auditCycleCounter = 0;
    }

    @Override
    public EnterpriseFamily getFamily() {
        return EnterpriseFamily.INSTANCE;
    }

    @Override
    public String getSensorId() {
        return sensorId;
    }

    @Override
    public SensorStatus getStatus() {
        return status;
    }

    @Override
    public TelemetryReading readTelemetry() {
        return TelemetryReading.of(
                sensorId,
                currentTempCelsius,
                1200.0,
                450.0,
                humidityPercent,
                "Relative Humidity (%)",
                status,
                "Enterprise ASHRAE A1 Compliant Envelope"
        );
    }

    @Override
    public TelemetryReading readTelemetry(PowerSupply<EnterpriseFamily> powerSupply) {
        // Enforced at compile-time: strictly requires EnterprisePowerSupply
        double derivedTemp = currentTempCelsius + (powerSupply.calculateHeatDissipationKw() * 0.08);
        this.currentTempCelsius = derivedTemp;

        if (derivedTemp > 35.0) {
            this.status = SensorStatus.CRITICAL_BREACH;
        } else if (derivedTemp > 28.0) {
            this.status = SensorStatus.WARNING_ELEVATED;
        } else {
            this.status = SensorStatus.ONLINE_NOMINAL;
        }

        return TelemetryReading.of(
                sensorId,
                derivedTemp,
                powerSupply.getSuppliedWattageKw(),
                powerSupply.calculateHeatDissipationKw(),
                humidityPercent,
                "Relative Humidity (%)",
                status,
                "Enterprise Telemetry: Power bank draw=" + powerSupply.getSuppliedWattageKw() + "kW"
        );
    }

    @Override
    public void calibrate(double baselineTolerance) {
        this.status = SensorStatus.CALIBRATING;
        this.calibrationTolerance = baselineTolerance;
        // Fine-tune offset to reference standard
        this.currentTempCelsius = 22.0 + baselineTolerance;
        this.status = SensorStatus.ONLINE_NOMINAL;
    }

    @Override
    public boolean isThresholdExceeded() {
        return currentTempCelsius > 27.0 || humidityPercent > 60.0;
    }

    @Override
    public void triggerDiagnosticAudit() {
        this.auditCycleCounter++;
        this.status = SensorStatus.ONLINE_NOMINAL;
    }

    public void simulateThermalSurge(double tempDelta) {
        this.currentTempCelsius += tempDelta;
        if (this.currentTempCelsius > 35.0) {
            this.status = SensorStatus.CRITICAL_BREACH;
        } else if (this.currentTempCelsius > 27.0) {
            this.status = SensorStatus.WARNING_ELEVATED;
        }
    }

    public double getCurrentTempCelsius() { return currentTempCelsius; }
    public double getHumidityPercent() { return humidityPercent; }
    public int getAuditCycleCounter() { return auditCycleCounter; }
}
