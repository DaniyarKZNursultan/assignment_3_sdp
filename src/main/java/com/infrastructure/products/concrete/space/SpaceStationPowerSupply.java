package com.infrastructure.products.concrete.space;

import com.infrastructure.family.SpaceStationFamily;
import com.infrastructure.model.PowerStatus;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;

/**
 * Concrete Product: Orbital Dual Photovoltaic & Cryogenic Fuel-Cell Power Bus.
 */
public class SpaceStationPowerSupply implements PowerSupply<SpaceStationFamily> {

    private final String powerArrayId;
    private final double maxCapacityKw;
    private double suppliedWattageKw;
    private final double efficiencyRating;
    private double fuelCellReservePercentage;
    private boolean eclipseModeActive;
    private PowerStatus status;

    public SpaceStationPowerSupply(String powerArrayId) {
        this.powerArrayId = powerArrayId;
        this.maxCapacityKw = 350.0;
        this.suppliedWattageKw = 140.0;
        this.efficiencyRating = 0.965; // High-efficiency Gallium Arsenide solar cells
        this.fuelCellReservePercentage = 94.0;
        this.eclipseModeActive = false;
        this.status = PowerStatus.GRID_STABLE;
    }

    @Override
    public SpaceStationFamily getFamily() {
        return SpaceStationFamily.INSTANCE;
    }

    @Override
    public String getModelName() {
        return "HelioVolt Dual Photovoltaic & Fuel-Cell Array (" + powerArrayId + ")";
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
        if (this.suppliedWattageKw < 80.0) {
            this.status = PowerStatus.DERATED_ECO;
        } else if (this.status != PowerStatus.BATTERY_BACKUP_ENGAGED) {
            this.status = PowerStatus.GRID_STABLE;
        }
    }

    @Override
    public void switchSourceToBackup(MonitoringSensor<SpaceStationFamily> sensor) {
        // Enforced at compile-time: strictly requires SpaceStation sensor
        this.eclipseModeActive = true;
        this.status = PowerStatus.BATTERY_BACKUP_ENGAGED;
        // Switch from main solar bus to cryogenic H2/O2 fuel-cells, shed non-essential science racks
        this.suppliedWattageKw = this.suppliedWattageKw * 0.70;
        this.fuelCellReservePercentage = Math.max(0.0, this.fuelCellReservePercentage - 1.0);
    }

    @Override
    public void triggerEmergencyShutdown() {
        this.status = PowerStatus.EMERGENCY_SHUTDOWN;
        this.suppliedWattageKw = 0.0;
        this.eclipseModeActive = false;
    }

    @Override
    public double calculateHeatDissipationKw() {
        return suppliedWattageKw * (1.0 - efficiencyRating);
    }

    public double getFuelCellReservePercentage() { return fuelCellReservePercentage; }
    public boolean isEclipseModeActive() { return eclipseModeActive; }
}
