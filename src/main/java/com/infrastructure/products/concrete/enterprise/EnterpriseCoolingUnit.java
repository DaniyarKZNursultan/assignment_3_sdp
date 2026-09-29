package com.infrastructure.products.concrete.enterprise;

import com.infrastructure.family.EnterpriseFamily;
import com.infrastructure.model.CoolingStatus;
import com.infrastructure.products.CoolingUnit;
import com.infrastructure.products.PowerSupply;

/**
 * Concrete Product: Enterprise Data Center Chilled-Water CRAH Unit.
 */
public class EnterpriseCoolingUnit implements CoolingUnit<EnterpriseFamily> {

    private final String loopId;
    private final double maxCapacityKw;
    private double currentCoolingLoadKw;
    private double glycolFlowRateLpm;
    private double chilledWaterTempCelsius;
    private CoolingStatus status;

    public EnterpriseCoolingUnit(String loopId) {
        this.loopId = loopId;
        this.maxCapacityKw = 800.0;
        this.currentCoolingLoadKw = 450.0;
        this.glycolFlowRateLpm = 1200.0;
        this.chilledWaterTempCelsius = 7.2;
        this.status = CoolingStatus.OPTIMAL;
    }

    @Override
    public EnterpriseFamily getFamily() {
        return EnterpriseFamily.INSTANCE;
    }

    @Override
    public String getModelName() {
        return "TitanChill-8000 Chilled Water CRAH (" + loopId + ")";
    }

    @Override
    public double getCurrentCoolingLoadKw() {
        return currentCoolingLoadKw;
    }

    @Override
    public double getMaxCapacityKw() {
        return maxCapacityKw;
    }

    @Override
    public CoolingStatus getStatus() {
        return status;
    }

    @Override
    public void activateCooling(double heatOutputKw) {
        this.currentCoolingLoadKw = Math.min(heatOutputKw, maxCapacityKw);
        this.glycolFlowRateLpm = 1000.0 + (this.currentCoolingLoadKw / maxCapacityKw) * 500.0;
        if (this.status != CoolingStatus.EMERGENCY_DISSIPATION) {
            this.status = (this.currentCoolingLoadKw > maxCapacityKw * 0.9)
                    ? CoolingStatus.ELEVATED_CHILL
                    : CoolingStatus.OPTIMAL;
        }
    }

    @Override
    public void emergencyHeatDissipation(double thermalSpikeKw) {
        this.status = CoolingStatus.EMERGENCY_DISSIPATION;
        this.glycolFlowRateLpm = 1800.0; // Max pump velocity
        this.chilledWaterTempCelsius = 4.5; // Chiller setpoint override
        this.currentCoolingLoadKw = Math.min(this.currentCoolingLoadKw + thermalSpikeKw, maxCapacityKw);
    }

    @Override
    public void coolPowerSupply(PowerSupply<EnterpriseFamily> powerSupply) {
        // Enforced at compile-time: can only cool EnterprisePowerSupply
        double heatLossKw = powerSupply.calculateHeatDissipationKw();
        activateCooling(heatLossKw * 1.35); // 35% margin for airflow turbulence in hyperscale hall
    }

    @Override
    public void shutdown() {
        this.status = CoolingStatus.SHUTDOWN;
        this.glycolFlowRateLpm = 0.0;
        this.currentCoolingLoadKw = 0.0;
    }

    public double getGlycolFlowRateLpm() { return glycolFlowRateLpm; }
    public double getChilledWaterTempCelsius() { return chilledWaterTempCelsius; }
}
