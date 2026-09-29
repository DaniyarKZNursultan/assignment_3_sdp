package com.infrastructure.client;

import com.infrastructure.abstractfactory.InfrastructureFactory;
import com.infrastructure.config.FactoryProvider;
import com.infrastructure.model.AuditTelemetryReport;
import com.infrastructure.model.EmergencyThrottlingReport;
import com.infrastructure.model.FailoverReport;
import com.infrastructure.service.InfrastructureOrchestrator;

/**
 * =========================================================================================
 * CLIENT APPLICATION: CRITICAL INFRASTRUCTURE MANAGEMENT CONSOLE
 * =========================================================================================
 *
 * PROOF THAT CLIENT OPERATES STRICTLY THROUGH ABSTRACTIONS:
 * 1. Notice there is NOT A SINGLE IMPORT of any concrete class (No EnterpriseCoolingUnit,
 *    EdgePowerSupply, ExtremeSensor, etc.).
 * 2. The client receives an {@link InfrastructureFactory} dynamically from {@link FactoryProvider}.
 * 3. The business operations are orchestrated entirely via {@link InfrastructureOrchestrator}
 *    and the generic product interfaces.
 * =========================================================================================
 */
public class InfrastructureManagementApplication {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   CRITICAL DATA CENTER INFRASTRUCTURE MANAGEMENT CONSOLE (v1.0.0)");
        System.out.println("   GoF Patterns: Factory Method & Abstract Factory Demonstration");
        System.out.println("================================================================================\n");

        // 1. Dynamic Factory Resolution (External Configuration)
        System.out.println("[Init] Resolving active infrastructure factory from external config.properties...");
        InfrastructureFactory<?> factory = (args.length > 0)
                ? FactoryProvider.getFactory(args[0])
                : FactoryProvider.getFactoryFromConfig();

        System.out.println("[Init] Resolved Factory Family: " + factory.getFamilyIdentifier() +
                " (" + factory.getFamily().getDescription() + ")\n");

        // 2. Initialize Business Orchestrator strictly via Abstractions
        String zoneId = "ZONE-ALPHA-PRIMARY";
        InfrastructureOrchestrator<?> orchestrator = InfrastructureOrchestrator.ofWildcard(factory, zoneId);

        System.out.println("[Equipment Deployment]");
        System.out.println(" - Cooling Unit:     " + orchestrator.getCoolingUnit().getModelName());
        System.out.println(" - Power Supply:     " + orchestrator.getPowerSupply().getModelName());
        System.out.println(" - Telemetry Sensor: " + orchestrator.getSensor().getSensorId() + " [" +
                orchestrator.getSensor().getStatus() + "]\n");

        // 3. Execute Business Operation 1: Routine Health & Efficiency Audit
        System.out.println(">>> EXECUTING BUSINESS OPERATION 1: Routine Health & Efficiency Audit...");
        AuditTelemetryReport auditReport = orchestrator.performRoutineHealthAudit();
        System.out.println("    Result: " + auditReport.diagnosisSummary());
        System.out.println("    Efficiency: " + auditReport.powerEfficiencyPercentage() + "%, Temp: " +
                auditReport.systemTemperatureCelsius() + "C\n");

        // 4. Execute Business Operation 2: Emergency Thermal Regulation & Power Throttling
        System.out.println(">>> EXECUTING BUSINESS OPERATION 2: Emergency Thermal Regulation Spike...");
        EmergencyThrottlingReport thermalReport = orchestrator.executeEmergencyThermalRegulation(120.0);
        System.out.println("    Result: " + thermalReport.incidentLog());
        System.out.println("    Thermal Runaway Averted: " + thermalReport.thermalRunawayAverted() + "\n");

        // 5. Execute Business Operation 3: Grid Failover & Load Shedding Sequence
        System.out.println(">>> EXECUTING BUSINESS OPERATION 3: Power Grid Failover & Load Shedding...");
        FailoverReport failoverReport = orchestrator.executeFailoverSequence("Sub-station transformer thermal trip");
        System.out.println("    Result: " + failoverReport.executionDetails());
        System.out.println("    Failover Successful: " + failoverReport.failoverSuccessful() + "\n");

        System.out.println("================================================================================");
        System.out.println("   ALL MULTI-PRODUCT OPERATIONS COMPLETED SUCCESSFULLY VIA ABSTRACTIONS.");
        System.out.println("================================================================================");
    }
}
