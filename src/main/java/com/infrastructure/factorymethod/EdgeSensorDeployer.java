package com.infrastructure.factorymethod;

import com.infrastructure.family.EdgeFamily;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.concrete.edge.EdgeSensor;

/**
 * Concrete Creator for Edge Computing IoT monitoring sensors.
 * Overrides the factory method {@link #createSensor} to instantiate {@link EdgeSensor}.
 */
public class EdgeSensorDeployer extends SensorDeploymentStation<EdgeFamily> {

    public EdgeSensorDeployer() {
        super(EdgeFamily.INSTANCE);
    }

    @Override
    protected MonitoringSensor<EdgeFamily> createSensor(String sensorId, double initialCalibrationOffset) {
        return new EdgeSensor(sensorId, initialCalibrationOffset);
    }
}
