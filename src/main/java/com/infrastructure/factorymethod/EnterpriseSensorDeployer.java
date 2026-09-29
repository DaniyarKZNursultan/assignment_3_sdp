package com.infrastructure.factorymethod;

import com.infrastructure.family.EnterpriseFamily;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.concrete.enterprise.EnterpriseSensor;

/**
 * Concrete Creator for Enterprise Data Center monitoring sensors.
 * Overrides the factory method {@link #createSensor} to instantiate {@link EnterpriseSensor}.
 */
public class EnterpriseSensorDeployer extends SensorDeploymentStation<EnterpriseFamily> {

    public EnterpriseSensorDeployer() {
        super(EnterpriseFamily.INSTANCE);
    }

    @Override
    protected MonitoringSensor<EnterpriseFamily> createSensor(String sensorId, double initialCalibrationOffset) {
        return new EnterpriseSensor(sensorId, initialCalibrationOffset);
    }
}
