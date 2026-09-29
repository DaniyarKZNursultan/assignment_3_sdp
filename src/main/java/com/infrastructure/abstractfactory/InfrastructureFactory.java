package com.infrastructure.abstractfactory;

import com.infrastructure.family.EnvironmentFamily;
import com.infrastructure.products.CoolingUnit;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;

/**
 * =========================================================================================
 * PART C & D: ABSTRACT FACTORY PATTERN WITH MANDATORY COMPATIBILITY RULE
 * =========================================================================================
 *
 * The Abstract Factory interface defines the creation contract for a complete, compatible
 * family of infrastructure products (CoolingUnit, PowerSupply, MonitoringSensor).
 *
 * -----------------------------------------------------------------------------------------
 * MANDATORY COMPATIBILITY RULE ARCHITECTURAL ENFORCEMENT:
 * -----------------------------------------------------------------------------------------
 * The GoF Abstract Factory ensures that products created by a factory are designed to work
 * together. In many basic tutorials, compatibility is either unenforced or checked via
 * runtime reflection / "if (!family.equals(other)) throw ...".
 *
 * In this production-grade architecture, compatibility is GUARANTEED STRUCTURALLY AND
 * AT COMPILE TIME via generic type parameterization {@code <F extends EnvironmentFamily>}:
 *
 * 1. An {@code InfrastructureFactory<EnterpriseFamily>} produces only:
 *    - {@code CoolingUnit<EnterpriseFamily>}
 *    - {@code PowerSupply<EnterpriseFamily>}
 *    - {@code MonitoringSensor<EnterpriseFamily>}
 *
 * 2. Product-to-product collaboration methods (e.g. {@code coolingUnit.coolPowerSupply(powerSupply)})
 *    strictly accept {@code PowerSupply<F>}.
 *
 * 3. Consequently, if a developer writes:
 *    {@code enterpriseCooling.coolPowerSupply(edgePowerSupply);}
 *    The Java compiler IMMEDIATELY rejects the code with a compile error:
 *    "incompatible types: PowerSupply<EdgeFamily> cannot be converted to PowerSupply<EnterpriseFamily>"
 *
 * 4. This eliminates entire classes of runtime configuration failures before deployment.
 * =========================================================================================
 *
 * @param <F> The concrete environment family boundary.
 */
public interface InfrastructureFactory<F extends EnvironmentFamily> {

    /**
     * Creates a family-compatible cooling unit.
     * @param unitId Unique identifier for the cooling equipment.
     * @return A cooling unit parameterized with family F.
     */
    CoolingUnit<F> createCoolingUnit(String unitId);

    /**
     * Creates a family-compatible power supply.
     * @param unitId Unique identifier for the power distribution equipment.
     * @return A power supply parameterized with family F.
     */
    PowerSupply<F> createPowerSupply(String unitId);

    /**
     * Creates a family-compatible monitoring sensor.
     * @param unitId Unique identifier for the sensor probe.
     * @return A monitoring sensor parameterized with family F.
     */
    MonitoringSensor<F> createMonitoringSensor(String unitId);

    /**
     * Returns the family marker associated with this factory.
     */
    F getFamily();

    /**
     * Returns the unique string identifier for this factory family (e.g., "ENTERPRISE").
     */
    String getFamilyIdentifier();
}
