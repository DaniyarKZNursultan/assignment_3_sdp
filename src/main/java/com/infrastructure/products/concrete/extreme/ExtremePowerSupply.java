package com.infrastructure.products.concrete.extreme;

import com.infrastructure.family.ExtremeFamily;
import com.infrastructure.model.PowerStatus;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;

/**
 * Concrete Product: Extreme Environment Radioisotope Thermoelectric Generator (RTG) & Fuel Feed.
 */
public class ExtremePowerSupply implements PowerSupply<ExtremeFamily> {

    private final String rtgUnitId;
    private final double maxCapacityKw;
    private double suppliedWattageKw;
    private final double efficiencyRating;
    private double containmentPressureBar;
    private PowerStatus status;

    public ExtremePowerSupply(String rtgUnitId) {
        this.rtgUnitId = rtgUnitId;
        this.maxCapacityKw = 1000.0;
        this.suppliedWattageKw = 450.0;
        this.efficiencyRating = 0.91; // 91% extreme-durability thermoelectric conversion
        this.containmentPressureBar = 95.0;
        this.status = PowerStatus.ISOTOPE_GENERATOR_PRIMARY;
    }

    @Override
    public ExtremeFamily getFamily() {
        return ExtremeFamily.INSTANCE;
    }

    @Override
    public String getModelName() {
        return "Aegis-RTG Thermoelectric Isotope Feed (" + rtgUnitId + ")";
    }

    @Override
    public double getSuppliedWattageKw() {
        return suppliedWattageKw;
    }

    @Override
    public double getMaxCapacityKw() {
        return maxCapacityKw;
    }

    @Override
    public double getEfficiencyRating() {
        return efficiencyRating;
    }

    @Override
    public PowerStatus getStatus() {
        return status;
    }

    @Override
    public void adjustOutput(double requiredKw) {
        this.suppliedWattageKw = Math.min(requiredKw, maxCapacityKw);
        if (this.suppliedWattageKw < 300.0) {
            this.status = PowerStatus.DERATED_ECO;
        } else if (this.status != PowerStatus.BATTERY_BACKUP_ENGAGED) {
            this.status = PowerStatus.ISOTOPE_GENERATOR_PRIMARY;
        }
    }

    @Override
    public void switchSourceToBackup(MonitoringSensor<ExtremeFamily> sensor) {
        // Enforced at compile-time: strictly requires Extreme sensor
        this.status = PowerStatus.BATTERY_BACKUP_ENGAGED;
        // Engage subterranean secondary molten-salt capacitor bank
        this.suppliedWattageKw = this.suppliedWattageKw * 0.75;
    }

    @Override
    public void triggerEmergencyShutdown() {
        this.status = PowerStatus.EMERGENCY_SHUTDOWN;
        this.suppliedWattageKw = 0.0;
        this.containmentPressureBar = 110.0; // Containment isolation valves closed
    }

    @Override
    public double calculateHeatDissipationKw() {
        return suppliedWattageKw * (1.0 - efficiencyRating);
    }

    public double getContainmentPressureBar() { return containmentPressureBar; }
}
