package com.infrastructure.family;

/**
 * Concrete family marker representing Subsea / Arctic Sub-Zero Extreme Stations.
 * Characteristics: Subterranean pressure tolerance, cryo-dielectric immersion cooling, isotope power.
 */
public final class ExtremeFamily implements EnvironmentFamily {
    public static final ExtremeFamily INSTANCE = new ExtremeFamily();

    private ExtremeFamily() {}

    @Override
    public String getFamilyCode() {
        return "EXTREME";
    }

    @Override
    public String getDescription() {
        return "Sub-Zero Arctic / Deep-Sea Extreme Environment Station";
    }

    @Override
    public String toString() {
        return getFamilyCode();
    }
}
