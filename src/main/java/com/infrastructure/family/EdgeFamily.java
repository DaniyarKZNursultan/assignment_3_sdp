package com.infrastructure.family;

/**
 * Concrete family marker representing Ruggedized 5G Micro Edge Nodes.
 * Characteristics: Ultra-compact footprint, 48V DC battery arrays, direct-expansion heat dissipation.
 */
public final class EdgeFamily implements EnvironmentFamily {
    public static final EdgeFamily INSTANCE = new EdgeFamily();

    private EdgeFamily() {}

    @Override
    public String getFamilyCode() {
        return "EDGE";
    }

    @Override
    public String getDescription() {
        return "Ruggedized 5G Micro-Edge Computing Node";
    }

    @Override
    public String toString() {
        return getFamilyCode();
    }
}
