package com.infrastructure.products;

import com.infrastructure.family.EnvironmentFamily;
import com.infrastructure.model.CoolingStatus;

/**
 * Product Type 1: Cooling Unit abstraction.
 *
 * PARAMETERIZED FAMILY COMPATIBILITY GUARANTEE:
 * Parameterized with {@code F extends EnvironmentFamily} so that interactions between
 * components (such as cooling a power supply) are enforced at compile time.
 * Attempting to pass a PowerSupply from a different family causes a Java compile error.
 *
 * @param <F> The concrete environment family this unit belongs to.
 */
public interface CoolingUnit<F extends EnvironmentFamily> {
    F getFamily();
    String getModelName();
    double getCurrentCoolingLoadKw();
    double getMaxCapacityKw();
    CoolingStatus getStatus();

    void activateCooling(double heatOutputKw);
    void emergencyHeatDissipation(double thermalSpikeKw);

    /**
     * Mandatory structural compatibility rule:
     * This cooling unit can only cool a PowerSupply belonging to the EXACT SAME family F.
     */
    void coolPowerSupply(PowerSupply<F> powerSupply);

    void shutdown();
}
