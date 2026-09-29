package com.infrastructure;

import com.infrastructure.abstractfactory.EdgeInfrastructureFactory;
import com.infrastructure.abstractfactory.EnterpriseInfrastructureFactory;
import com.infrastructure.abstractfactory.ExtremeInfrastructureFactory;
import com.infrastructure.abstractfactory.InfrastructureFactory;
import com.infrastructure.config.ConfigurationException;
import com.infrastructure.config.ConfigurationLoader;
import com.infrastructure.config.FactoryProvider;
import com.infrastructure.config.UnknownFamilyException;
import com.infrastructure.factorymethod.EdgeSensorDeployer;
import com.infrastructure.factorymethod.EnterpriseSensorDeployer;
import com.infrastructure.factorymethod.ExtremeSensorDeployer;
import com.infrastructure.model.AuditTelemetryReport;
import com.infrastructure.model.CoolingStatus;
import com.infrastructure.model.DeploymentReport;
import com.infrastructure.model.EmergencyThrottlingReport;
import com.infrastructure.model.FailoverReport;
import com.infrastructure.model.PowerStatus;
import com.infrastructure.model.SensorStatus;
import com.infrastructure.model.TelemetryReading;
import com.infrastructure.products.CoolingUnit;
import com.infrastructure.products.MonitoringSensor;
import com.infrastructure.products.PowerSupply;
import com.infrastructure.products.concrete.edge.EdgeCoolingUnit;
import com.infrastructure.products.concrete.edge.EdgePowerSupply;
import com.infrastructure.products.concrete.edge.EdgeSensor;
import com.infrastructure.products.concrete.enterprise.EnterpriseCoolingUnit;
import com.infrastructure.products.concrete.enterprise.EnterprisePowerSupply;
import com.infrastructure.products.concrete.enterprise.EnterpriseSensor;
import com.infrastructure.products.concrete.extreme.ExtremeCoolingUnit;
import com.infrastructure.products.concrete.extreme.ExtremePowerSupply;
import com.infrastructure.products.concrete.extreme.ExtremeSensor;
import com.infrastructure.service.InfrastructureOrchestrator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Multi-Family Extensible Infrastructure System Test Suite")
class InfrastructureSystemTest {

    // =========================================================================
    // 1. ABSTRACT FACTORY & PRODUCT FAMILY CREATION TESTS
    // =========================================================================
    @Nested
    @DisplayName("Abstract Factory: Creation of Original Product Families")
    class AbstractFactoryCreationTests {

        @Test
        @DisplayName("TC-01: Enterprise Infrastructure Factory creates compatible Enterprise products")
        void testEnterpriseFamilyProductCreation() {
            EnterpriseInfrastructureFactory factory = new EnterpriseInfrastructureFactory();

            CoolingUnit<?> cooling = factory.createCoolingUnit("CW-101");
            PowerSupply<?> power = factory.createPowerSupply("UPS-101");
            MonitoringSensor<?> sensor = factory.createMonitoringSensor("SNMP-101");

            assertNotNull(cooling);
            assertNotNull(power);
            assertNotNull(sensor);

            assertInstanceOf(EnterpriseCoolingUnit.class, cooling);
            assertInstanceOf(EnterprisePowerSupply.class, power);
            assertInstanceOf(EnterpriseSensor.class, sensor);

            assertEquals("ENTERPRISE", factory.getFamilyIdentifier());
            assertEquals("ENTERPRISE", cooling.getFamily().getFamilyCode());
            assertEquals("ENTERPRISE", power.getFamily().getFamilyCode());
            assertEquals("ENTERPRISE", sensor.getFamily().getFamilyCode());
        }

        @Test
        @DisplayName("TC-02: Edge Infrastructure Factory creates compatible Edge products")
        void testEdgeFamilyProductCreation() {
            EdgeInfrastructureFactory factory = new EdgeInfrastructureFactory();

            CoolingUnit<?> cooling = factory.createCoolingUnit("DX-201");
            PowerSupply<?> power = factory.createPowerSupply("DC-201");
            MonitoringSensor<?> sensor = factory.createMonitoringSensor("PROBE-201");

            assertInstanceOf(EdgeCoolingUnit.class, cooling);
            assertInstanceOf(EdgePowerSupply.class, power);
            assertInstanceOf(EdgeSensor.class, sensor);

            assertEquals("EDGE", factory.getFamilyIdentifier());
            assertEquals("EDGE", cooling.getFamily().getFamilyCode());
        }

        @Test
        @DisplayName("TC-03: Extreme Infrastructure Factory creates compatible Extreme products")
        void testExtremeFamilyProductCreation() {
            ExtremeInfrastructureFactory factory = new ExtremeInfrastructureFactory();

            CoolingUnit<?> cooling = factory.createCoolingUnit("CRYO-301");
            PowerSupply<?> power = factory.createPowerSupply("RTG-301");
            MonitoringSensor<?> sensor = factory.createMonitoringSensor("OPT-301");

            assertInstanceOf(ExtremeCoolingUnit.class, cooling);
            assertInstanceOf(ExtremePowerSupply.class, power);
            assertInstanceOf(ExtremeSensor.class, sensor);

            assertEquals("EXTREME", factory.getFamilyIdentifier());
            assertEquals("EXTREME", cooling.getFamily().getFamilyCode());
        }
    }

    // =========================================================================
    // 2. CONCRETE PRODUCT STATE AND BEHAVIOR TESTS
    // =========================================================================
    @Nested
    @DisplayName("Concrete Product State and Behavior Verification")
    class ConcreteProductBehaviorTests {

        @Test
        @DisplayName("TC-04: Enterprise products exhibit distinct state and operational dynamics")
        void testEnterpriseProductBehavior() {
            EnterpriseCoolingUnit cooling = new EnterpriseCoolingUnit("CW-ENT");
            EnterprisePowerSupply power = new EnterprisePowerSupply("UPS-ENT");
            EnterpriseSensor sensor = new EnterpriseSensor("SENS-ENT", 0.05);

            assertEquals(800.0, cooling.getMaxCapacityKw());
            assertEquals(2000.0, power.getMaxCapacityKw());
            assertEquals(0.975, power.getEfficiencyRating());

            // Thermal dissipation calculation
            double heatKw = power.calculateHeatDissipationKw();
            assertEquals(1200.0 * (1.0 - 0.975), heatKw, 0.001);

            // Typed inter-product cooling
            cooling.coolPowerSupply(power);
            assertTrue(cooling.getGlycolFlowRateLpm() > 1000.0);

            // Integrated telemetry reading
            TelemetryReading reading = sensor.readTelemetry(power);
            assertNotNull(reading);
            assertEquals(SensorStatus.ONLINE_NOMINAL, reading.status());
        }

        @Test
        @DisplayName("TC-05: Edge products exhibit compact low-power and PWM fan dynamics")
        void testEdgeProductBehavior() {
            EdgeCoolingUnit cooling = new EdgeCoolingUnit("DX-EDGE");
            EdgePowerSupply power = new EdgePowerSupply("DC-EDGE");
            EdgeSensor sensor = new EdgeSensor("SENS-EDGE", 0.1);

            assertEquals(25.0, cooling.getMaxCapacityKw());
            assertEquals(30.0, power.getMaxCapacityKw());
            assertEquals(0.94, power.getEfficiencyRating());

            cooling.coolPowerSupply(power);
            assertTrue(cooling.getFanRpm() >= 2500);

            // Edge sensor low-latency check
            TelemetryReading reading = sensor.readTelemetry(power);
            assertTrue(reading.temperatureCelsius() > 30.0);
        }

        @Test
        @DisplayName("TC-06: Extreme products exhibit cryo-pressure and isotope generator dynamics")
        void testExtremeProductBehavior() {
            ExtremeCoolingUnit cooling = new ExtremeCoolingUnit("CRYO-EXT");
            ExtremePowerSupply power = new ExtremePowerSupply("RTG-EXT");
            ExtremeSensor sensor = new ExtremeSensor("SENS-EXT", 0.02);

            assertEquals(600.0, cooling.getMaxCapacityKw());
            assertEquals(PowerStatus.ISOTOPE_GENERATOR_PRIMARY, power.getStatus());
            assertTrue(cooling.getCryoFluidTempCelsius() < -30.0);

            cooling.coolPowerSupply(power);
            assertTrue(cooling.getImmersionPressureBar() > 10.0);
        }
    }

    // =========================================================================
    // 3. FACTORY METHOD PATTERN & CREATOR LIFECYCLE TESTS
    // =========================================================================
    @Nested
    @DisplayName("Factory Method Pattern: Creator Business Lifecycle")
    class FactoryMethodTests {

        @Test
        @DisplayName("TC-07: EnterpriseSensorDeployer completes full deployment, calibration and audit")
        void testEnterpriseSensorDeployerLifecycle() {
            EnterpriseSensorDeployer deployer = new EnterpriseSensorDeployer();
            DeploymentReport<?> report = deployer.deployAndCalibrateSensor("SENS-ENT-001", 0.05);

            assertNotNull(report);
            assertEquals("SENS-ENT-001", report.sensorId());
            assertEquals("ENTERPRISE", report.family().getFamilyCode());
            assertTrue(report.selfTestPassed());
            assertNotNull(report.verificationChecksum());
            assertFalse(report.verificationChecksum().isEmpty());
            assertTrue(report.auditLog().contains("Certified deployment"));
        }

        @Test
        @DisplayName("TC-08: EdgeSensorDeployer and ExtremeSensorDeployer polymorphic execution")
        void testOtherDeployersLifecycle() {
            EdgeSensorDeployer edgeDeployer = new EdgeSensorDeployer();
            DeploymentReport<?> edgeReport = edgeDeployer.deployAndCalibrateSensor("SENS-EDGE-001", 0.1);
            assertEquals("EDGE", edgeReport.family().getFamilyCode());
            assertTrue(edgeReport.selfTestPassed());

            ExtremeSensorDeployer extremeDeployer = new ExtremeSensorDeployer();
            DeploymentReport<?> extremeReport = extremeDeployer.deployAndCalibrateSensor("SENS-EXT-001", -0.05);
            assertEquals("EXTREME", extremeReport.family().getFamilyCode());
            assertTrue(extremeReport.selfTestPassed());
        }

        @Test
        @DisplayName("TC-09 (Negative Scenario 1): Factory Method rejects out-of-envelope calibration offset")
        void testFactoryMethodRejectsInvalidCalibrationOffset() {
            EnterpriseSensorDeployer deployer = new EnterpriseSensorDeployer();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    deployer.deployAndCalibrateSensor("SENS-BAD-01", 99.0));
            assertTrue(ex.getMessage().contains("exceeds permissible safety envelope"));
        }

        @Test
        @DisplayName("TC-10 (Negative Scenario 2): Factory Method rejects blank or null sensor IDs")
        void testFactoryMethodRejectsBlankSensorId() {
            EnterpriseSensorDeployer deployer = new EnterpriseSensorDeployer();
            assertThrows(IllegalArgumentException.class, () ->
                    deployer.deployAndCalibrateSensor("   ", 0.0));
            assertThrows(IllegalArgumentException.class, () ->
                    deployer.deployAndCalibrateSensor(null, 0.0));
        }
    }

    // =========================================================================
    // 4. RUNTIME FACTORY SELECTION & CONFIGURATION TESTS
    // =========================================================================
    @Nested
    @DisplayName("Runtime Factory Selection & Configuration")
    class RuntimeFactorySelectionTests {

        @ParameterizedTest
        @ValueSource(strings = {"ENTERPRISE", "EDGE", "EXTREME"})
        @DisplayName("TC-11: FactoryProvider dynamically resolves all core families by key")
        void testDynamicResolutionByKey(String familyKey) {
            InfrastructureFactory<?> factory = FactoryProvider.getFactory(familyKey);
            assertNotNull(factory);
            assertEquals(familyKey, factory.getFamilyIdentifier());
        }

        @Test
        @DisplayName("TC-12: FactoryProvider loads active family dynamically from config.properties")
        void testLoadFromClasspathConfiguration() {
            InfrastructureFactory<?> factory = FactoryProvider.getFactoryFromConfig();
            assertNotNull(factory);
            // Default configured in config.properties is ENTERPRISE
            assertEquals("ENTERPRISE", factory.getFamilyIdentifier());
        }

        @Test
        @DisplayName("TC-13 (Negative Scenario 3): Requesting an unknown family key throws UnknownFamilyException")
        void testUnknownFamilyThrowsException() {
            UnknownFamilyException ex = assertThrows(UnknownFamilyException.class, () ->
                    FactoryProvider.getFactory("CYBER_UNDERWATER_HABITAT"));
            assertTrue(ex.getMessage().contains("Unknown infrastructure family"));
        }

        @Test
        @DisplayName("TC-14 (Negative Scenario 4): Requesting null/empty family throws UnknownFamilyException")
        void testNullOrEmptyFamilyThrowsException() {
            assertThrows(UnknownFamilyException.class, () -> FactoryProvider.getFactory(null));
            assertThrows(UnknownFamilyException.class, () -> FactoryProvider.getFactory("  "));
        }

        @Test
        @DisplayName("TC-15 (Negative Scenario 5): Loading non-existent config file throws ConfigurationException")
        void testMissingConfigFileThrowsConfigurationException() {
            assertThrows(ConfigurationException.class, () ->
                    ConfigurationLoader.loadConfiguredFamily("non_existent_config.properties"));
        }
    }

    // =========================================================================
    // 5. MULTI-PRODUCT BUSINESS SCENARIOS (INTEGRATION COLLABORATION)
    // =========================================================================
    @Nested
    @DisplayName("Part F: Multi-Product Collaborative Business Operations")
    class BusinessScenarioTests {

        @Test
        @DisplayName("TC-16: Business Operation 1 — Emergency Thermal Regulation & Power Throttling")
        void testBusinessOperation1EmergencyThermalRegulation() {
            InfrastructureFactory<?> factory = FactoryProvider.getFactory("ENTERPRISE");
            InfrastructureOrchestrator<?> orchestrator = InfrastructureOrchestrator.ofWildcard(factory, "ZONE-CRITICAL");

            double initialWattage = orchestrator.getPowerSupply().getSuppliedWattageKw();
            EmergencyThrottlingReport report = orchestrator.executeEmergencyThermalRegulation(150.0);

            assertNotNull(report);
            assertTrue(report.thermalRunawayAverted(), "Thermal runaway must be averted");
            assertTrue(report.throttledPowerKw() < initialWattage, "Power must be throttled down");
            assertEquals(CoolingStatus.EMERGENCY_DISSIPATION, orchestrator.getCoolingUnit().getStatus());
            assertTrue(report.coolingDissipatedKw() > 150.0);
        }

        @Test
        @DisplayName("TC-17: Business Operation 2 — Routine Health & Efficiency Telemetry Audit")
        void testBusinessOperation2RoutineHealthAudit() {
            InfrastructureFactory<?> factory = FactoryProvider.getFactory("EDGE");
            InfrastructureOrchestrator<?> orchestrator = InfrastructureOrchestrator.ofWildcard(factory, "ZONE-EDGE-7");

            AuditTelemetryReport report = orchestrator.performRoutineHealthAudit();

            assertNotNull(report);
            assertTrue(report.healthAuditPassed(), "Routine audit must pass for nominal Edge system");
            assertTrue(report.powerEfficiencyPercentage() >= 90.0);
            assertTrue(report.coolingLoadKw() <= orchestrator.getCoolingUnit().getMaxCapacityKw());
            assertEquals(CoolingStatus.OPTIMAL, orchestrator.getCoolingUnit().getStatus());
        }

        @Test
        @DisplayName("TC-18: Business Operation 3 — Power Grid Failover & Load Shedding")
        void testBusinessOperation3FailoverSequence() {
            InfrastructureFactory<?> factory = FactoryProvider.getFactory("EXTREME");
            InfrastructureOrchestrator<?> orchestrator = InfrastructureOrchestrator.ofWildcard(factory, "ZONE-ARCTIC");

            FailoverReport report = orchestrator.executeFailoverSequence("Subsea cable severance event");

            assertNotNull(report);
            assertTrue(report.failoverSuccessful());
            assertEquals(PowerStatus.BATTERY_BACKUP_ENGAGED, report.postFailoverPowerStatus());
            assertTrue(report.shedNonEssentialWattageKw() > 0.0, "Workloads must be shed to extend backup life");
            assertNotEquals(CoolingStatus.SHUTDOWN, report.postFailoverCoolingStatus());
        }
    }

    // =========================================================================
    // 6. CLIENT ABSTRACTION & COMPATIBILITY GUARANTEES
    // =========================================================================
    @Nested
    @DisplayName("Client Abstraction & Structural Compatibility")
    class ClientAbstractionAndCompatibilityTests {

        @Test
        @DisplayName("TC-19: Client Orchestrator operates seamlessly across all three core families through abstractions")
        void testClientWorksThroughPureAbstractions() {
            for (String familyKey : new String[]{"ENTERPRISE", "EDGE", "EXTREME"}) {
                InfrastructureFactory<?> factory = FactoryProvider.getFactory(familyKey);
                InfrastructureOrchestrator<?> orchestrator = InfrastructureOrchestrator.ofWildcard(factory, "ZONE-" + familyKey);

                AuditTelemetryReport audit = orchestrator.performRoutineHealthAudit();
                assertTrue(audit.healthAuditPassed());

                EmergencyThrottlingReport thermal = orchestrator.executeEmergencyThermalRegulation(50.0);
                assertTrue(thermal.thermalRunawayAverted());

                FailoverReport failover = orchestrator.executeFailoverSequence("Standard Test Drill");
                assertTrue(failover.failoverSuccessful());
            }
        }

        @Test
        @DisplayName("TC-20: Compatibility Rule — Concrete products from identical family collaborate cleanly")
        void testFamilyCompatibilityVerification() {
            EnterpriseInfrastructureFactory factory = new EnterpriseInfrastructureFactory();
            EnterpriseCoolingUnit cooling = (EnterpriseCoolingUnit) factory.createCoolingUnit("CW-COMPAT");
            EnterprisePowerSupply power = (EnterprisePowerSupply) factory.createPowerSupply("UPS-COMPAT");
            EnterpriseSensor sensor = (EnterpriseSensor) factory.createMonitoringSensor("SENS-COMPAT");

            // Direct family interaction verified
            assertDoesNotThrow(() -> cooling.coolPowerSupply(power));
            assertDoesNotThrow(() -> power.switchSourceToBackup(sensor));
            assertDoesNotThrow(() -> sensor.readTelemetry(power));

            // Verify compile-time type binding: all share EnterpriseFamily
            assertEquals(cooling.getFamily().getClass(), power.getFamily().getClass());
            assertEquals(power.getFamily().getClass(), sensor.getFamily().getClass());
        }
    }
}
