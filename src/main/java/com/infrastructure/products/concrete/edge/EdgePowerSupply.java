package com.infrastructure.products.concrete.edge;

import com.infrastructure.family.EdgeFamily;
import com.infrastructure.model.PowerStatus;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;

/**
 * Concrete Product: Edge Computing 48V DC Micro-Battery Bus.
 */
public class EdgePowerSupply implements PowerSupply<EdgeFamily> {

    private final String busSerial;
    private final double maxCapacityKw;
    private double suppliedWattageKw;
    private final double efficiencyRating;
    private double batteryChargePercent;
    private PowerStatus status;

    public EdgePowerSupply(String busSerial) {
        this.busSerial = busSerial;
        this.maxCapacityKw = 30.0;
        this.suppliedWattageKw = 14.0;
        this.efficiencyRating = 0.94; // 94% compact DC conversion
        this.batteryChargePercent = 98.0;
        this.status = PowerStatus.GRID_STABLE;
    }

    @Override
    public EdgeFamily getFamily() {
        return EdgeFamily.INSTANCE;
    }

    @Override
    public String getModelName() {
        return "VoltCompact 48V DC Micro-Battery Bus (" + busSerial + ")";
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
        if (this.suppliedWattageKw < 8.0) {
            this.status = PowerStatus.DERATED_ECO;
        } else if (this.status != PowerStatus.BATTERY_BACKUP_ENGAGED) {
            this.status = PowerStatus.GRID_STABLE;
        }
    }

    @Override
    public void switchSourceToBackup(MonitoringSensor<EdgeFamily> sensor) {
        // Enforced at compile-time: strictly requires Edge sensor
        this.status = PowerStatus.BATTERY_BACKUP_ENGAGED;
        // Edge units shed auxiliary workloads: cut power by 30% to conserve LiFePO4 pack
        this.suppliedWattageKw = this.suppliedWattageKw * 0.70;
        this.batteryChargePercent = Math.max(0.0, this.batteryChargePercent - 1.5);
    }

    @Override
    public void triggerEmergencyShutdown() {
        this.status = PowerStatus.EMERGENCY_SHUTDOWN;
        this.suppliedWattageKw = 0.0;
    }

    @Override
    public double calculateHeatDissipationKw() {
        return suppliedWattageKw * (1.0 - efficiencyRating);
    }

    public double getBatteryChargePercent() { return batteryChargePercent; }
}
