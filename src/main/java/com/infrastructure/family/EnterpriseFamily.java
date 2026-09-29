package com.infrastructure.family;

/**
 * Concrete family marker representing Tier-IV Enterprise Mega Data Centers.
 * Characteristics: Multi-megawatt feeds, chilled-water CRAH loops, SNMP telemetry.
 */
public final class EnterpriseFamily implements EnvironmentFamily {
    public static final EnterpriseFamily INSTANCE = new EnterpriseFamily();

    private EnterpriseFamily() {}

    @Override
    public String getFamilyCode() {
        return "ENTERPRISE";
    }

    @Override
    public String getDescription() {
        return "Tier-IV Hyperscale Enterprise Data Center Facility";
    }

    @Override
    public String toString() {
        return getFamilyCode();
    }
}
