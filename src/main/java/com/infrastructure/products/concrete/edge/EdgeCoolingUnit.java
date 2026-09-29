package com.infrastructure.products.concrete.edge;

import com.infrastructure.family.EdgeFamily;
import com.infrastructure.model.CoolingStatus;
import com.infrastructure.products.CoolingUnit;
import com.infrastructure.products.PowerSupply;

/**
 * Concrete Product: Edge Computing Direct-Expansion Micro-Chiller.
 */
public class EdgeCoolingUnit implements CoolingUnit<EdgeFamily> {

    private final String fanArrayId;
    private final double maxCapacityKw;
    private double currentCoolingLoadKw;
    private int fanRpm;
    private double refrigerantPressurePsi;
    private CoolingStatus status;

    public EdgeCoolingUnit(String fanArrayId) {
        this.fanArrayId = fanArrayId;
        this.maxCapacityKw = 25.0;
        this.currentCoolingLoadKw = 8.5;
        this.fanRpm = 3200;
        this.refrigerantPressurePsi = 110.0;
        this.status = CoolingStatus.OPTIMAL;
    }

    @Override
    public EdgeFamily getFamily() {
        return EdgeFamily.INSTANCE;
    }

    @Override
    public String getModelName() {
        return "AeroEdge DX Micro-Chiller (" + fanArrayId + ")";
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
        this.fanRpm = 2500 + (int) ((this.currentCoolingLoadKw / maxCapacityKw) * 3500.0);
        if (this.status != CoolingStatus.EMERGENCY_DISSIPATION) {
            this.status = (this.currentCoolingLoadKw > maxCapacityKw * 0.85)
                    ? CoolingStatus.ELEVATED_CHILL
                    : CoolingStatus.OPTIMAL;
        }
    }

    @Override
    public void emergencyHeatDissipation(double thermalSpikeKw) {
        this.status = CoolingStatus.EMERGENCY_DISSIPATION;
        this.fanRpm = 6000; // Emergency turbo fan RPM
        this.refrigerantPressurePsi = 145.0;
        this.currentCoolingLoadKw = Math.min(this.currentCoolingLoadKw + thermalSpikeKw, maxCapacityKw);
    }

    @Override
    public void coolPowerSupply(PowerSupply<EdgeFamily> powerSupply) {
        // Enforced at compile-time: can only cool EdgePowerSupply
        double dissipation = powerSupply.calculateHeatDissipationKw();
        activateCooling(dissipation * 1.15); // Compact enclosure heat trap margin
    }

    @Override
    public void shutdown() {
        this.status = CoolingStatus.SHUTDOWN;
        this.fanRpm = 0;
        this.currentCoolingLoadKw = 0.0;
    }

    public int getFanRpm() { return fanRpm; }
    public double getRefrigerantPressurePsi() { return refrigerantPressurePsi; }
}
