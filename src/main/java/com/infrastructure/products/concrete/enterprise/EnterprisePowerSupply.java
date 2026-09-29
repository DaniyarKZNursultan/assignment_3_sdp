package com.infrastructure.products.concrete.enterprise;

import com.infrastructure.family.EnterpriseFamily;
import com.infrastructure.model.PowerStatus;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;

/**
 * Concrete Product: Enterprise Data Center Hyperscale UPS & 3-Phase Bus.
 */
public class EnterprisePowerSupply implements PowerSupply<EnterpriseFamily> {

    private final String powerBankId;
    private final double maxCapacityKw;
    private double suppliedWattageKw;
    private final double efficiencyRating;
    private PowerStatus status;
    private boolean flywheelReserveEngaged;

    public EnterprisePowerSupply(String powerBankId) {
        this.powerBankId = powerBankId;
        this.maxCapacityKw = 2000.0;
        this.suppliedWattageKw = 1200.0;
        this.efficiencyRating = 0.975; // 97.5% high-efficiency double-conversion
        this.status = PowerStatus.GRID_STABLE;
        this.flywheelReserveEngaged = false;
    }

    @Override
    public EnterpriseFamily getFamily() {
        return EnterpriseFamily.INSTANCE;
    }

    @Override
    public String getModelName() {
        return "MegawattGrid-X 3-Phase Hyperscale UPS (" + powerBankId + ")";
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
        if (this.suppliedWattageKw < 800.0) {
            this.status = PowerStatus.DERATED_ECO;
        } else if (this.status != PowerStatus.BATTERY_BACKUP_ENGAGED) {
            this.status = PowerStatus.GRID_STABLE;
        }
    }

    @Override
    public void switchSourceToBackup(MonitoringSensor<EnterpriseFamily> sensor) {
        // Enforced at compile-time: can only interact with Enterprise sensor
        this.flywheelReserveEngaged = true;
        this.status = PowerStatus.BATTERY_BACKUP_ENGAGED;
        // Derate by 20% to conserve backup runtime
        this.suppliedWattageKw = this.suppliedWattageKw * 0.80;
    }

    @Override
    public void triggerEmergencyShutdown() {
        this.status = PowerStatus.EMERGENCY_SHUTDOWN;
        this.suppliedWattageKw = 0.0;
        this.flywheelReserveEngaged = false;
    }

    @Override
    public double calculateHeatDissipationKw() {
        // Active thermal waste = suppliedWattage * (1 - efficiency)
        return suppliedWattageKw * (1.0 - efficiencyRating);
    }

    public boolean isFlywheelReserveEngaged() { return flywheelReserveEngaged; }
}
