package com.infrastructure.abstractfactory;

import com.infrastructure.family.EnterpriseFamily;
import com.infrastructure.products.CoolingUnit;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;
import com.infrastructure.products.concrete.enterprise.EnterpriseCoolingUnit;
import com.infrastructure.products.concrete.enterprise.EnterprisePowerSupply;
import com.infrastructure.products.concrete.enterprise.EnterpriseSensor;

/**
 * Concrete Factory: Produces a fully compatible suite of Enterprise Data Center equipment.
 */
public class EnterpriseInfrastructureFactory implements InfrastructureFactory<EnterpriseFamily> {

    public static final String IDENTIFIER = "ENTERPRISE";

    @Override
    public CoolingUnit<EnterpriseFamily> createCoolingUnit(String unitId) {
        return new EnterpriseCoolingUnit(unitId);
    }

    @Override
    public PowerSupply<EnterpriseFamily> createPowerSupply(String unitId) {
        return new EnterprisePowerSupply(unitId);
    }

    @Override
    public MonitoringSensor<EnterpriseFamily> createMonitoringSensor(String unitId) {
        return new EnterpriseSensor(unitId, 0.05);
    }

    @Override
    public EnterpriseFamily getFamily() {
        return EnterpriseFamily.INSTANCE;
    }

    @Override
    public String getFamilyIdentifier() {
        return IDENTIFIER;
    }
}
