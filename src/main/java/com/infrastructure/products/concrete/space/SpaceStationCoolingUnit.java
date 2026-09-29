package com.infrastructure.products.concrete.space;

import com.infrastructure.family.SpaceStationFamily;
import com.infrastructure.model.CoolingStatus;
import com.infrastructure.products.CoolingUnit;
import com.infrastructure.products.PowerSupply;

/**
 * Concrete Product: Orbital Deployable Ammonia Radiator Loop.
 * Radiates thermal waste directly into the 3 Kelvin cold vacuum of deep space.
 */
public class SpaceStationCoolingUnit implements CoolingUnit<SpaceStationFamily> {

    private final String radiatorId;
    private final double maxCapacityKw;
    private double currentCoolingLoadKw;
    private double ammoniaFlowRateKgSec;
    private double radiatorSurfaceTempKelvin;
    private boolean sunshieldDeployed;
    private CoolingStatus status;

    public SpaceStationCoolingUnit(String radiatorId) {
        this.radiatorId = radiatorId;
        this.maxCapacityKw = 250.0;
        this.currentCoolingLoadKw = 95.0;
        this.ammoniaFlowRateKgSec = 8.5;
        this.radiatorSurfaceTempKelvin = 180.0;
        this.sunshieldDeployed = true;
        this.status = CoolingStatus.OPTIMAL;
    }

    @Override
    public SpaceStationFamily getFamily() {
        return SpaceStationFamily.INSTANCE;
    }

    @Override
    public String getModelName() {
        return "CosmoCool Deployable Ammonia Radiator (" + radiatorId + ")";
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
        this.ammoniaFlowRateKgSec = 5.0 + (this.currentCoolingLoadKw / maxCapacityKw) * 10.0;
        if (this.status != CoolingStatus.EMERGENCY_DISSIPATION) {
            this.status = (this.currentCoolingLoadKw > maxCapacityKw * 0.85)
                    ? CoolingStatus.ELEVATED_CHILL
                    : CoolingStatus.OPTIMAL;
        }
    }

    @Override
    public void emergencyHeatDissipation(double thermalSpikeKw) {
        this.status = CoolingStatus.EMERGENCY_DISSIPATION;
        this.ammoniaFlowRateKgSec = 16.0; // Maximum pump speed through radiator wings
        this.radiatorSurfaceTempKelvin = 240.0; // Stefan-Boltzmann emission T^4 max
        this.currentCoolingLoadKw = Math.min(this.currentCoolingLoadKw + thermalSpikeKw, maxCapacityKw);
    }

    @Override
    public void coolPowerSupply(PowerSupply<SpaceStationFamily> powerSupply) {
        // Enforced at compile-time: can only cool SpaceStationPowerSupply
        double heatKw = powerSupply.calculateHeatDissipationKw();
        activateCooling(heatKw * 1.40); // 40% safety margin for direct solar flare exposure
    }

    @Override
    public void shutdown() {
        this.status = CoolingStatus.SHUTDOWN;
        this.ammoniaFlowRateKgSec = 0.0;
        this.currentCoolingLoadKw = 0.0;
    }

    public double getAmmoniaFlowRateKgSec() { return ammoniaFlowRateKgSec; }
    public double getRadiatorSurfaceTempKelvin() { return radiatorSurfaceTempKelvin; }
    public boolean isSunshieldDeployed() { return sunshieldDeployed; }
}
