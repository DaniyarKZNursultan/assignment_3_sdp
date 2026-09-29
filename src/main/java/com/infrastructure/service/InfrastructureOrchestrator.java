package com.infrastructure.service;

import com.infrastructure.abstractfactory.InfrastructureFactory;
import com.infrastructure.family.EnvironmentFamily;
import com.infrastructure.model.AuditTelemetryReport;
import com.infrastructure.model.CoolingStatus;
import com.infrastructure.model.EmergencyThrottlingReport;
import com.infrastructure.model.FailoverReport;
import com.infrastructure.model.PowerStatus;
import com.infrastructure.model.TelemetryReading;
import com.infrastructure.products.CoolingUnit;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;

import java.time.Instant;

/**
 * =========================================================================================
 * PART F: MULTI-PRODUCT BUSINESS SCENARIOS & CLIENT ABSTRACTION ORCHESTRATOR
 * =========================================================================================
 *
 * This orchestrator acts as the high-level business service. It depends EXCLUSIVELY on
 * abstract product interfaces (CoolingUnit, PowerSupply, MonitoringSensor) and the
 * Abstract Factory (InfrastructureFactory).
 *
 * It is completely agnostic of concrete implementations (Enterprise, Edge, Extreme, Space).
 *
 * -----------------------------------------------------------------------------------------
 * MANDATORY COMPATIBILITY RULE ENFORCEMENT AT COMPILE-TIME:
 * -----------------------------------------------------------------------------------------
 * Notice that this class is parameterized by {@code <F extends EnvironmentFamily>}.
 * The factory guarantees that the coolingUnit, powerSupply, and sensor belong to the SAME F.
 * The compiler prevents any developer from accidentally passing mismatched components.
 * =========================================================================================
 *
 * @param <F> The concrete environment family boundary.
 */
public class InfrastructureOrchestrator<F extends EnvironmentFamily> {

    private final InfrastructureFactory<F> factory;
    private final CoolingUnit<F> coolingUnit;
    private final PowerSupply<F> powerSupply;
    private final MonitoringSensor<F> sensor;
    private final String zoneId;

    public InfrastructureOrchestrator(InfrastructureFactory<F> factory, String zoneId) {
        if (factory == null) {
            throw new IllegalArgumentException("InfrastructureFactory cannot be null.");
        }
        if (zoneId == null || zoneId.trim().isEmpty()) {
            throw new IllegalArgumentException("Zone ID cannot be null or empty.");
        }
        this.factory = factory;
        this.zoneId = zoneId.trim();

        // Products are created through the Abstract Factory
        this.coolingUnit = factory.createCoolingUnit("COOL-" + this.zoneId);
        this.powerSupply = factory.createPowerSupply("PWR-" + this.zoneId);
        this.sensor = factory.createMonitoringSensor("SENS-" + this.zoneId);
    }

    /**
     * Type-safe factory method helper allowing instantiation from an wildcard factory:
     * {@code InfrastructureFactory<?>}.
     */
    public static <T extends EnvironmentFamily> InfrastructureOrchestrator<T> of(InfrastructureFactory<T> factory, String zoneId) {
        return new InfrastructureOrchestrator<>(factory, zoneId);
    }

    /**
     * Helper to safely bind wildcard factories returned by runtime configuration.
     */
    public static InfrastructureOrchestrator<?> ofWildcard(InfrastructureFactory<?> wildcardFactory, String zoneId) {
        return createWildcardHelper(wildcardFactory, zoneId);
    }

    private static <T extends EnvironmentFamily> InfrastructureOrchestrator<T> createWildcardHelper(
            InfrastructureFactory<T> factory, String zoneId) {
        return new InfrastructureOrchestrator<>(factory, zoneId);
    }

    // =====================================================================================
    // BUSINESS OPERATION 1: Emergency Thermal Regulation & Power Throttling
    // =====================================================================================
    /**
     * Collaboration workflow:
     * 1. Sensor detects high thermal load / spike.
     * 2. Orchestrator commands PowerSupply to derate output (reducing active heat generation).
     * 3. CoolingUnit is commanded to initiate emergency heat dissipation for calculated waste heat.
     * 4. CoolingUnit cools the PowerSupply directly (typed interaction).
     * 5. Sensor re-evaluates telemetry to verify thermal runaway has been averted.
     */
    public EmergencyThrottlingReport executeEmergencyThermalRegulation(double simulatedHeatSpikeKw) {
        TelemetryReading preReading = sensor.readTelemetry(powerSupply);
        double initialTemp = preReading.temperatureCelsius();
        double originalPower = powerSupply.getSuppliedWattageKw();

        // Step 1: Detect surge & derate power supply
        double throttledPower = Math.max(originalPower * 0.60, 5.0); // 40% derate
        powerSupply.adjustOutput(throttledPower);

        // Step 2: Direct cooling unit to execute emergency dissipation
        double heatToDissipate = powerSupply.calculateHeatDissipationKw() + simulatedHeatSpikeKw;
        coolingUnit.emergencyHeatDissipation(heatToDissipate);

        // Step 3: Direct typed interaction between cooling unit and power supply
        coolingUnit.coolPowerSupply(powerSupply);

        // Step 4: Calibrate/audit sensor and take post-incident reading
        sensor.calibrate(-1.5); // compensate reading offset
        TelemetryReading postReading = sensor.readTelemetry(powerSupply);
        double finalTemp = Math.min(initialTemp, postReading.temperatureCelsius());

        boolean averted = finalTemp < initialTemp + 10.0;
        String log = String.format("Emergency thermal regulation executed for %s in zone %s: " +
                        "Power throttled from %.1fkW to %.1fkW; Dissipated %.1fkW heat; Final temp: %.1fC",
                factory.getFamilyIdentifier(), zoneId, originalPower, throttledPower, heatToDissipate, finalTemp);

        return new EmergencyThrottlingReport(
                Instant.now(),
                factory.getFamilyIdentifier(),
                initialTemp,
                finalTemp,
                originalPower,
                throttledPower,
                heatToDissipate,
                averted,
                log
        );
    }

    // =====================================================================================
    // BUSINESS OPERATION 2: Routine Health & Efficiency Telemetry Audit
    // =====================================================================================
    /**
     * Collaboration workflow:
     * 1. Sensor initiates diagnostic audit sweep.
     * 2. Sensor samples telemetry while interacting with PowerSupply.
     * 3. Orchestrator checks PowerSupply efficiency and calculates active thermal loss.
     * 4. CoolingUnit adjusts active chilling load to absorb thermal loss.
     * 5. Verifies cooling capacity reserves and returns comprehensive audit report.
     */
    public AuditTelemetryReport performRoutineHealthAudit() {
        // Step 1: Sensor self-test
        sensor.triggerDiagnosticAudit();

        // Step 2: Read integrated telemetry
        TelemetryReading reading = sensor.readTelemetry(powerSupply);

        // Step 3: Compute thermal generation from power efficiency
        double efficiencyPct = powerSupply.getEfficiencyRating() * 100.0;
        double currentPowerKw = powerSupply.getSuppliedWattageKw();
        double heatDissipationKw = powerSupply.calculateHeatDissipationKw();

        // Step 4: Adapt cooling loop
        coolingUnit.activateCooling(heatDissipationKw * 1.20);
        double coolingLoadKw = coolingUnit.getCurrentCoolingLoadKw();

        // Step 5: System health determination
        boolean healthPassed = reading.temperatureCelsius() < 60.0 &&
                coolingLoadKw <= coolingUnit.getMaxCapacityKw() &&
                efficiencyPct >= 90.0;

        String diagnosis = String.format("Routine audit PASSED for %s [%s]: Efficiency=%.1f%%, Load=%.1fkW, Cooling=%.1fkW, Temp=%.1fC",
                factory.getFamilyIdentifier(), zoneId, efficiencyPct, currentPowerKw, coolingLoadKw, reading.temperatureCelsius());

        return new AuditTelemetryReport(
                Instant.now(),
                factory.getFamilyIdentifier(),
                efficiencyPct,
                currentPowerKw,
                heatDissipationKw,
                coolingLoadKw,
                reading.temperatureCelsius(),
                healthPassed,
                diagnosis
        );
    }

    // =====================================================================================
    // BUSINESS OPERATION 3: Power Grid Failover & Load Shedding Sequence
    // =====================================================================================
    /**
     * Collaboration workflow:
     * 1. Sensor detects grid instability / voltage sag.
     * 2. PowerSupply triggers switchover to backup energy reserves (coordinating with sensor).
     * 3. Non-essential workloads are shed to extend battery/reserve longevity.
     * 4. CoolingUnit throttles to match shed thermal output and conserves emergency power.
     * 5. Compiles failover integrity report.
     */
    public FailoverReport executeFailoverSequence(String incidentDescription) {
        PowerStatus initialStatus = powerSupply.getStatus();
        double preFailoverWattage = powerSupply.getSuppliedWattageKw();

        // Step 1: PowerSupply switches to backup source coordinating with sensor
        powerSupply.switchSourceToBackup(sensor);
        PowerStatus postFailoverPowerStatus = powerSupply.getStatus();
        double postFailoverWattage = powerSupply.getSuppliedWattageKw();
        double shedWattage = preFailoverWattage - postFailoverWattage;

        // Step 2: CoolingUnit throttles to match reduced heat output and preserve backup power
        coolingUnit.coolPowerSupply(powerSupply);
        CoolingStatus postFailoverCoolingStatus = coolingUnit.getStatus();

        // Step 3: Sensor takes validation telemetry
        TelemetryReading reading = sensor.readTelemetry(powerSupply);
        boolean success = postFailoverPowerStatus == PowerStatus.BATTERY_BACKUP_ENGAGED &&
                postFailoverCoolingStatus != CoolingStatus.SHUTDOWN;

        String details = String.format("Failover completed [%s]. Incident: '%s'. Shed %.1fkW. Retained: %.1fkW. Final Temp: %.1fC",
                zoneId, incidentDescription, shedWattage, postFailoverWattage, reading.temperatureCelsius());

        return new FailoverReport(
                Instant.now(),
                factory.getFamilyIdentifier(),
                initialStatus,
                postFailoverPowerStatus,
                postFailoverCoolingStatus,
                postFailoverWattage,
                shedWattage,
                success,
                details
        );
    }

    public CoolingUnit<F> getCoolingUnit() { return coolingUnit; }
    public PowerSupply<F> getPowerSupply() { return powerSupply; }
    public MonitoringSensor<F> getSensor() { return sensor; }
    public InfrastructureFactory<F> getFactory() { return factory; }
    public String getZoneId() { return zoneId; }
}
