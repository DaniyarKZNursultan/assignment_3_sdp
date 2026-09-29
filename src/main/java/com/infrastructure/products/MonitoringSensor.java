package com.infrastructure.products;

import com.infrastructure.family.EnvironmentFamily;
import com.infrastructure.model.SensorStatus;
import com.infrastructure.model.TelemetryReading;

/**
 * Product Type 3: Monitoring Sensor abstraction.
 *
 * PARAMETERIZED FAMILY COMPATIBILITY GUARANTEE:
 * Parameterized with {@code F extends EnvironmentFamily}. Provides telemetry reading
 * and calibration workflows bound to components of the same family.
 *
 * @param <F> The concrete environment family this unit belongs to.
 */
public interface MonitoringSensor<F extends EnvironmentFamily> {
    F getFamily();
    String getSensorId();
    SensorStatus getStatus();

    TelemetryReading readTelemetry();

    /**
     * Mandatory structural compatibility rule:
     * Cross-component telemetry analysis strictly requires a PowerSupply of the SAME family F.
     */
    TelemetryReading readTelemetry(PowerSupply<F> powerSupply);

    void calibrate(double baselineTolerance);
    boolean isThresholdExceeded();
    void triggerDiagnosticAudit();
}
