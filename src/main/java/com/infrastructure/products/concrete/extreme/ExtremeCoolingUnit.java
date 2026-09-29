package com.infrastructure.products.concrete.extreme;

import com.infrastructure.family.ExtremeFamily;
import com.infrastructure.model.CoolingStatus;
import com.infrastructure.products.CoolingUnit;
import com.infrastructure.products.PowerSupply;

/**
 * Concrete Product: Extreme Subsea/Arctic Cryo-Dielectric Immersion Bath.
 */
public class ExtremeCoolingUnit implements CoolingUnit<ExtremeFamily> {

    private final String bathSerial;
    private final double maxCapacityKw;
    private double currentCoolingLoadKw;
    private double cryoFluidTempCelsius;
    private double immersionPressureBar;
    private CoolingStatus status;

    public ExtremeCoolingUnit(String bathSerial) {
        this.bathSerial = bathSerial;
        this.maxCapacityKw = 600.0;
        this.currentCoolingLoadKw = 280.0;
        this.cryoFluidTempCelsius = -38.0;
        this.immersionPressureBar = 15.0;
        this.status = CoolingStatus.OPTIMAL;
    }

    @Override
    public ExtremeFamily getFamily() {
        return ExtremeFamily.INSTANCE;
    }

    @Override
    public String getModelName() {
        return "SubZero Cryo-Dielectric Immersion Bath (" + bathSerial + ")";
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
        this.immersionPressureBar = 12.0 + (this.currentCoolingLoadKw / maxCapacityKw) * 10.0;
        if (this.status != CoolingStatus.EMERGENCY_DISSIPATION) {
            this.status = (this.currentCoolingLoadKw > maxCapacityKw * 0.9)
                    ? CoolingStatus.ELEVATED_CHILL
                    : CoolingStatus.OPTIMAL;
        }
    }

    @Override
    public void emergencyHeatDissipation(double thermalSpikeKw) {
        this.status = CoolingStatus.EMERGENCY_DISSIPATION;
        this.cryoFluidTempCelsius = -50.0; // Cryogenic boost phase
        this.immersionPressureBar = 25.0;
        this.currentCoolingLoadKw = Math.min(this.currentCoolingLoadKw + thermalSpikeKw, maxCapacityKw);
    }

    @Override
    public void coolPowerSupply(PowerSupply<ExtremeFamily> powerSupply) {
        // Enforced at compile-time: can only cool ExtremePowerSupply
        double dissipation = powerSupply.calculateHeatDissipationKw();
        activateCooling(dissipation * 1.5); // Cryogenic heat siphon transfer buffer
    }

    @Override
    public void shutdown() {
        this.status = CoolingStatus.SHUTDOWN;
        this.immersionPressureBar = 1.0;
        this.currentCoolingLoadKw = 0.0;
    }

    public double getCryoFluidTempCelsius() { return cryoFluidTempCelsius; }
    public double getImmersionPressureBar() { return immersionPressureBar; }
}
