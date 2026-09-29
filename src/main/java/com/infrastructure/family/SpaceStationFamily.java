package com.infrastructure.family;

/**
 * Concrete family marker representing Orbital Space Stations and Satellite Nodes.
 * Characteristics: Vacuum thermal radiation, dual photovoltaic/fuel-cell bus, cosmic ray sensors.
 */
public final class SpaceStationFamily implements EnvironmentFamily {
    public static final SpaceStationFamily INSTANCE = new SpaceStationFamily();

    private SpaceStationFamily() {}

    @Override
    public String getFamilyCode() {
        return "SPACE_STATION";
    }

    @Override
    public String getDescription() {
        return "Orbital Space Station & Deep-Space Satellite Infrastructure";
    }

    @Override
    public String toString() {
        return getFamilyCode();
    }
}
