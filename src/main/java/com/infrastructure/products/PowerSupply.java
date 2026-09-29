package com.infrastructure.products;

import com.infrastructure.family.EnvironmentFamily;
import com.infrastructure.model.PowerStatus;

/**
 * Product Type 2: Power Supply abstraction.
 *
 * PARAMETERIZED FAMILY COMPATIBILITY GUARANTEE:
 * Parameterized with {@code F extends EnvironmentFamily} to structurally bind
 * power delivery and failover operations to identical family components.
 *
 * @param <F> The concrete environment family this unit belongs to.
 */
public interface PowerSupply<F extends EnvironmentFamily> {
    F getFamily();
    String getModelName();
    double getSuppliedWattageKw();
    double getMaxCapacityKw();
    double getEfficiencyRating(); // e.g. 0.96 for 96%
    PowerStatus getStatus();

    void adjustOutput(double requiredKw);

    /**
     * Mandatory structural compatibility rule:
     * Failover coordination requires a MonitoringSensor belonging to the EXACT SAME family F.
     */
    void switchSourceToBackup(MonitoringSensor<F> sensor);

    void triggerEmergencyShutdown();
    double calculateHeatDissipationKw();
}
