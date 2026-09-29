package com.infrastructure.abstractfactory;

import com.infrastructure.family.SpaceStationFamily;
import com.infrastructure.products.CoolingUnit;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;
import com.infrastructure.products.concrete.space.SpaceStationCoolingUnit;
import com.infrastructure.products.concrete.space.SpaceStationPowerSupply;
import com.infrastructure.products.concrete.space.SpaceStationSensor;

/**
 * =========================================================================================
 * PART G: 4TH PRODUCT FAMILY EXTENSION (DEMONSTRATION OF OPEN/CLOSED PRINCIPLE)
 * =========================================================================================
 *
 * This concrete factory adds support for the 4th infrastructure family: Orbital Space Stations.
 *
 * -----------------------------------------------------------------------------------------
 * ARCHITECTURAL IMPACT MATRIX (Open/Closed Principle Audit):
 * -----------------------------------------------------------------------------------------
 * NEW FILES ADDED (System extended without modifying existing business rules):
 *   1. com.infrastructure.family.SpaceStationFamily
 *   2. com.infrastructure.products.concrete.space.SpaceStationCoolingUnit
 *   3. com.infrastructure.products.concrete.space.SpaceStationPowerSupply
 *   4. com.infrastructure.products.concrete.space.SpaceStationSensor
 *   5. com.infrastructure.factorymethod.SpaceStationSensorDeployer
 *   6. com.infrastructure.abstractfactory.SpaceStationInfrastructureFactory
 *   7. com.infrastructure.SpaceStationExtensionTest
 *
 * FILES MODIFIED (Restricted strictly to configuration/registration hook):
 *   1. com.infrastructure.config.FactoryProvider (added 1 line in static block to register supplier)
 *
 * FILES UNTOUCHED (Zero modifications required — 100% OCP Compliant):
 *   - All Product Interfaces (CoolingUnit, PowerSupply, MonitoringSensor)
 *   - Abstract Factory Interface (InfrastructureFactory)
 *   - Creator Abstract Class (SensorDeploymentStation)
 *   - All existing 3 families (Enterprise, Edge, Extreme classes untouched)
 *   - High-level Business Service (InfrastructureOrchestrator)
 *   - Client Console Application (InfrastructureManagementApplication)
 * =========================================================================================
 */
public class SpaceStationInfrastructureFactory implements InfrastructureFactory<SpaceStationFamily> {

    public static final String IDENTIFIER = "SPACE_STATION";

    @Override
    public CoolingUnit<SpaceStationFamily> createCoolingUnit(String unitId) {
        return new SpaceStationCoolingUnit(unitId);
    }

    @Override
    public PowerSupply<SpaceStationFamily> createPowerSupply(String unitId) {
        return new SpaceStationPowerSupply(unitId);
    }

    @Override
    public MonitoringSensor<SpaceStationFamily> createMonitoringSensor(String unitId) {
        return new SpaceStationSensor(unitId, 0.01);
    }

    @Override
    public SpaceStationFamily getFamily() {
        return SpaceStationFamily.INSTANCE;
    }

    @Override
    public String getFamilyIdentifier() {
        return IDENTIFIER;
    }
}
