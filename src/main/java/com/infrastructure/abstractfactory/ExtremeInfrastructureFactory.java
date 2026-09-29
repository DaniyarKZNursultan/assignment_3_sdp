package com.infrastructure.abstractfactory;

import com.infrastructure.family.ExtremeFamily;
import com.infrastructure.products.CoolingUnit;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;
import com.infrastructure.products.concrete.extreme.ExtremeCoolingUnit;
import com.infrastructure.products.concrete.extreme.ExtremePowerSupply;
import com.infrastructure.products.concrete.extreme.ExtremeSensor;

/**
 * Concrete Factory: Produces a fully compatible suite of Subsea/Arctic Extreme Station equipment.
 */
public class ExtremeInfrastructureFactory implements InfrastructureFactory<ExtremeFamily> {

    public static final String IDENTIFIER = "EXTREME";

    @Override
    public CoolingUnit<ExtremeFamily> createCoolingUnit(String unitId) {
        return new ExtremeCoolingUnit(unitId);
    }

    @Override
    public PowerSupply<ExtremeFamily> createPowerSupply(String unitId) {
        return new ExtremePowerSupply(unitId);
    }

    @Override
    public MonitoringSensor<ExtremeFamily> createMonitoringSensor(String unitId) {
        return new ExtremeSensor(unitId, 0.02);
    }

    @Override
    public ExtremeFamily getFamily() {
        return ExtremeFamily.INSTANCE;
    }

    @Override
    public String getFamilyIdentifier() {
        return IDENTIFIER;
    }
}
