package com.infrastructure;

import com.infrastructure.abstractfactory.InfrastructureFactory;
import com.infrastructure.abstractfactory.SpaceStationInfrastructureFactory;
import com.infrastructure.config.FactoryProvider;
import com.infrastructure.factorymethod.SpaceStationSensorDeployer;
import com.infrastructure.model.AuditTelemetryReport;
import com.infrastructure.model.CoolingStatus;
import com.infrastructure.model.DeploymentReport;
import com.infrastructure.model.EmergencyThrottlingReport;
import com.infrastructure.model.FailoverReport;
import com.infrastructure.model.PowerStatus;
import com.infrastructure.model.TelemetryReading;
import com.infrastructure.products.CoolingUnit;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;
import com.infrastructure.products.concrete.space.SpaceStationCoolingUnit;
import com.infrastructure.products.concrete.space.SpaceStationPowerSupply;
import com.infrastructure.products.concrete.space.SpaceStationSensor;
import com.infrastructure.service.InfrastructureOrchestrator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Part G: 4th Product Family Extension (Space Station) Test Suite")
class SpaceStationExtensionTest {

    @Nested
    @DisplayName("Creation and Concrete Product Tests")
    class CreationTests {

        @Test
        @DisplayName("TC-EXT-01: SpaceStationInfrastructureFactory creates compatible Space Station products")
        void testSpaceStationFamilyCreation() {
            InfrastructureFactory<?> factory = FactoryProvider.getFactory("SPACE_STATION");
            assertNotNull(factory);
            assertInstanceOf(SpaceStationInfrastructureFactory.class, factory);
            assertEquals("SPACE_STATION", factory.getFamilyIdentifier());

            CoolingUnit<?> cooling = factory.createCoolingUnit("RAD-ORBIT-1");
            PowerSupply<?> power = factory.createPowerSupply("SOLAR-BUS-1");
            MonitoringSensor<?> sensor = factory.createMonitoringSensor("COSMIC-SENS-1");

            assertInstanceOf(SpaceStationCoolingUnit.class, cooling);
            assertInstanceOf(SpaceStationPowerSupply.class, power);
            assertInstanceOf(SpaceStationSensor.class, sensor);

            assertEquals("SPACE_STATION", cooling.getFamily().getFamilyCode());
            assertEquals("SPACE_STATION", power.getFamily().getFamilyCode());
            assertEquals("SPACE_STATION", sensor.getFamily().getFamilyCode());
        }

        @Test
        @DisplayName("TC-EXT-02: Space Station products exhibit orbital radiator and solar array dynamics")
        void testSpaceStationOperationalState() {
            SpaceStationCoolingUnit cooling = new SpaceStationCoolingUnit("RAD-TEST");
            SpaceStationPowerSupply power = new SpaceStationPowerSupply("PWR-TEST");
            SpaceStationSensor sensor = new SpaceStationSensor("SENS-TEST", 0.01);

            assertEquals(250.0, cooling.getMaxCapacityKw());
            assertEquals(350.0, power.getMaxCapacityKw());
            assertEquals(0.965, power.getEfficiencyRating());
            assertTrue(cooling.isSunshieldDeployed());

            cooling.coolPowerSupply(power);
            assertTrue(cooling.getAmmoniaFlowRateKgSec() >= 5.0);

            TelemetryReading reading = sensor.readTelemetry(power);
            assertNotNull(reading);
            assertTrue(reading.auxiliaryMetric() >= 400.0, "Should measure cosmic radiation flux");
        }
    }

    @Nested
    @DisplayName("Factory Method & Business Scenarios for 4th Family")
    class ExtensionBusinessTests {

        @Test
        @DisplayName("TC-EXT-03: SpaceStationSensorDeployer executes Factory Method lifecycle")
        void testSpaceStationSensorDeployer() {
            SpaceStationSensorDeployer deployer = new SpaceStationSensorDeployer();
            DeploymentReport<?> report = deployer.deployAndCalibrateSensor("SENS-ORBIT-99", 0.02);

            assertNotNull(report);
            assertEquals("SPACE_STATION", report.family().getFamilyCode());
            assertTrue(report.selfTestPassed());
            assertNotNull(report.verificationChecksum());
            assertTrue(report.auditLog().contains("Certified deployment"));
        }

        @Test
        @DisplayName("TC-EXT-04: Untouched InfrastructureOrchestrator executes all 3 operations on Space Station")
        void testOrchestratorIntegrationWithNewFamily() {
            InfrastructureFactory<?> factory = FactoryProvider.getFactory("SPACE_STATION");
            InfrastructureOrchestrator<?> orchestrator = InfrastructureOrchestrator.ofWildcard(factory, "ORBITAL-MODULE-A");

            // Operation 1: Routine Health Audit
            AuditTelemetryReport audit = orchestrator.performRoutineHealthAudit();
            assertTrue(audit.healthAuditPassed(), "Space station routine health audit must pass");
            assertEquals("SPACE_STATION", audit.familyCode());

            // Operation 2: Emergency Thermal Regulation (Solar Flare Spike)
            EmergencyThrottlingReport thermal = orchestrator.executeEmergencyThermalRegulation(60.0);
            assertTrue(thermal.thermalRunawayAverted(), "Radiator loops must dissipate solar flare heat");
            assertEquals(CoolingStatus.EMERGENCY_DISSIPATION, orchestrator.getCoolingUnit().getStatus());

            // Operation 3: Grid Failover (Day-to-Night Eclipse Transition to Fuel Cells)
            FailoverReport failover = orchestrator.executeFailoverSequence("Orbital Eclipse: Entering Earth's umbra shadow");
            assertTrue(failover.failoverSuccessful());
            assertEquals(PowerStatus.BATTERY_BACKUP_ENGAGED, failover.postFailoverPowerStatus());
            assertTrue(failover.shedNonEssentialWattageKw() > 0.0);
        }
    }
}
