package com.infrastructure.factorymethod;

import com.infrastructure.family.SpaceStationFamily;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.concrete.space.SpaceStationSensor;

/**
 * Concrete Creator for Orbital Space Station radiation & microgravity sensors.
 * Demonstrates the Factory Method pattern for the 4th product family.
 */
public class SpaceStationSensorDeployer extends SensorDeploymentStation<SpaceStationFamily> {

    public SpaceStationSensorDeployer() {
        super(SpaceStationFamily.INSTANCE);
    }

    @Override
    protected MonitoringSensor<SpaceStationFamily> createSensor(String sensorId, double initialCalibrationOffset) {
        return new SpaceStationSensor(sensorId, initialCalibrationOffset);
    }
}
