package com.infrastructure.factorymethod;

import com.infrastructure.family.ExtremeFamily;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.concrete.extreme.ExtremeSensor;

/**
 * Concrete Creator for Extreme Subsea/Arctic optical & pressure sensors.
 * Overrides the factory method {@link #createSensor} to instantiate {@link ExtremeSensor}.
 */
public class ExtremeSensorDeployer extends SensorDeploymentStation<ExtremeFamily> {

    public ExtremeSensorDeployer() {
        super(ExtremeFamily.INSTANCE);
    }

    @Override
    protected MonitoringSensor<ExtremeFamily> createSensor(String sensorId, double initialCalibrationOffset) {
        return new ExtremeSensor(sensorId, initialCalibrationOffset);
    }
}
