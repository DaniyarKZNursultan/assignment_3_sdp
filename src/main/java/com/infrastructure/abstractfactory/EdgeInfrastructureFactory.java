package com.infrastructure.abstractfactory;

import com.infrastructure.family.EdgeFamily;
import com.infrastructure.products.CoolingUnit;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;
import com.infrastructure.products.concrete.edge.EdgeCoolingUnit;
import com.infrastructure.products.concrete.edge.EdgePowerSupply;
import com.infrastructure.products.concrete.edge.EdgeSensor;

/**
 * Concrete Factory: Produces a fully compatible suite of Ruggedized Edge Node equipment.
 */
public class EdgeInfrastructureFactory implements InfrastructureFactory<EdgeFamily> {

    public static final String IDENTIFIER = "EDGE";

    @Override
    public CoolingUnit<EdgeFamily> createCoolingUnit(String unitId) {
        return new EdgeCoolingUnit(unitId);
    }

    @Override
    public PowerSupply<EdgeFamily> createPowerSupply(String unitId) {
        return new EdgePowerSupply(unitId);
    }

    @Override
    public MonitoringSensor<EdgeFamily> createMonitoringSensor(String unitId) {
        return new EdgeSensor(unitId, 0.10);
    }

    @Override
    public EdgeFamily getFamily() {
        return EdgeFamily.INSTANCE;
    }

    @Override
    public String getFamilyIdentifier() {
        return IDENTIFIER;
    }
}
