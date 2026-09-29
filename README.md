# Multi-Family Extensible System: Smart Critical Infrastructure & Data Center Management

> **Academic Assignment 2**: GoF Factory Method & Abstract Factory  
> **Course**: Software Design Patterns & Architecture  
> **Tech Stack**: Java 17 LTS, JUnit 5, Apache Maven, Git  

---

## 1. Domain Model & Product Matrix

The system models a mission-critical **Smart Data Center and Remote Infrastructure Management System**. It manages the automated provisioning, telemetry monitoring, thermal regulation, and emergency failover of high-performance computing facilities across divergent operational environments.

### The 3 × 4 Product Matrix (12 Concrete Products)

| Environment / Family | Product 1: `CoolingUnit<F>` | Product 2: `PowerSupply<F>` | Product 3: `MonitoringSensor<F>` |
| :--- | :--- | :--- | :--- |
| **Family A: Enterprise Data Center** (`ENTERPRISE`) | `EnterpriseCoolingUnit`<br>*(TitanChill-8000 Chilled-Water CRAH loop)* | `EnterprisePowerSupply`<br>*(MegawattGrid-X 3-Phase Hyperscale UPS)* | `EnterpriseSensor`<br>*(OmniSense SNMP Telemetry Array)* |
| **Family B: Edge Computing Node** (`EDGE`) | `EdgeCoolingUnit`<br>*(AeroEdge DX Micro-Chiller & PWM fans)* | `EdgePowerSupply`<br>*(VoltCompact 48V DC Micro-Battery Bus)* | `EdgeSensor`<br>*(NanoProbe 5G IoT Telemetry Probe)* |
| **Family C: Extreme Environment Station** (`EXTREME`) | `ExtremeCoolingUnit`<br>*(SubZero Cryo-Dielectric Immersion Bath)* | `ExtremePowerSupply`<br>*(Aegis-RTG Thermoelectric Isotope Feed)* | `ExtremeSensor`<br>*(TitanHardened Optical Radiation Probe)* |
| **Family D: Orbital Space Station** (`SPACE_STATION`) *(Part G Extension)* | `SpaceStationCoolingUnit`<br>*(CosmoCool Deployable Ammonia Radiator)* | `SpaceStationPowerSupply`<br>*(HelioVolt Dual Photovoltaic/Fuel-Cell Bus)* | `SpaceStationSensor`<br>*(StellarGuard Cosmic Ray & Microgravity Sensor)* |

---

## 2. Project Architecture & Pattern Implementation

```
src/
├── main/
│   ├── java/com/infrastructure/
│   │   ├── abstractfactory/         # Part C & D: Abstract Factory Interface & 4 Concrete Factories
│   │   │   ├── InfrastructureFactory.java
│   │   │   ├── EnterpriseInfrastructureFactory.java
│   │   │   ├── EdgeInfrastructureFactory.java
│   │   │   ├── ExtremeInfrastructureFactory.java
│   │   │   └── SpaceStationInfrastructureFactory.java  # Part G Extension
│   │   ├── client/                  # Main Application Client
│   │   │   └── InfrastructureManagementApplication.java
│   │   ├── config/                  # Part E: Dynamic Factory Selection & Exceptions
│   │   │   ├── ConfigurationException.java
│   │   │   ├── ConfigurationLoader.java
│   │   │   ├── FactoryProvider.java
│   │   │   └── UnknownFamilyException.java
│   │   ├── factorymethod/           # Part B: Factory Method Abstract Creator & Concrete Creators
│   │   │   ├── EdgeSensorDeployer.java
│   │   │   ├── EnterpriseSensorDeployer.java
│   │   │   ├── ExtremeSensorDeployer.java
│   │   │   ├── SensorDeploymentStation.java            # Abstract Creator with lifecycle logic
│   │   │   └── SpaceStationSensorDeployer.java         # Part G Extension
│   │   ├── family/                  # Compile-Time Family Type Boundaries
│   │   │   ├── EdgeFamily.java
│   │   │   ├── EnterpriseFamily.java
│   │   │   ├── EnvironmentFamily.java                  # Root Family Marker Interface
│   │   │   ├── ExtremeFamily.java
│   │   │   └── SpaceStationFamily.java
│   │   ├── legacy/                  # Part A: Anti-pattern code with direct instantiation flaws
│   │   │   ├── LegacyEdgeCoolingUnit.java
│   │   │   ├── LegacyEdgePowerSupply.java
│   │   │   ├── LegacyEdgeSensor.java
│   │   │   ├── LegacyEnterpriseCoolingUnit.java
│   │   │   ├── LegacyEnterprisePowerSupply.java
│   │   │   ├── LegacyEnterpriseSensor.java
│   │   │   ├── LegacyExtremeCoolingUnit.java
│   │   │   ├── LegacyExtremePowerSupply.java
│   │   │   ├── LegacyExtremeSensor.java
│   │   │   └── LegacySystemClient.java
│   │   ├── model/                   # Domain Telemetry Records & Operational Reports
│   │   │   ├── AuditTelemetryReport.java
│   │   │   ├── CoolingStatus.java
│   │   │   ├── DeploymentReport.java
│   │   │   ├── EmergencyThrottlingReport.java
│   │   │   ├── FailoverReport.java
│   │   │   ├── PowerStatus.java
│   │   │   ├── SensorStatus.java
│   │   │   └── TelemetryReading.java
│   │   ├── products/                # Product Abstractions
│   │   │   ├── CoolingUnit.java
│   │   │   ├── MonitoringSensor.java
│   │   │   ├── PowerSupply.java
│   │   │   └── concrete/            # 12 Concrete Products organized by family
│   │   │       ├── edge/
│   │   │       ├── enterprise/
│   │   │       ├── extreme/
│   │   │       └── space/
│   │   └── service/                 # Part F: High-Level Business Orchestrator
│   │       └── InfrastructureOrchestrator.java
│   └── resources/
│       └── config.properties        # External Runtime Configuration
└── test/
    └── java/com/infrastructure/     # 26 Comprehensive JUnit 5 Tests
        ├── InfrastructureSystemTest.java
        └── SpaceStationExtensionTest.java
```

---

## 3. Detailed Assignment Walkthrough

### Part A: Start Without Factories (`LegacySystemClient`)
The class `LegacySystemClient` showcases how direct object creation with `new` leads to 3 fatal architectural flaws:
1. **Tight Coupling to Concrete Implementations**: The client imports 9+ concrete classes, violating the Dependency Inversion Principle (DIP). Modifying any constructor breaks the client.
2. **Code Duplication & OCP Violation**: Adding a 4th family forces editing monolithic `if-else` / `switch` blocks in multiple client methods.
3. **Catastrophic Cross-Family Incompatibility Risk**: Nothing stops a developer from writing `new LegacyEnterpriseCoolingUnit()` and passing it to a 15kW `LegacyEdgePowerSupply()`, causing severe overcooling and equipment burnout in production.

### Part B: Factory Method (`SensorDeploymentStation`)
- **Abstract Creator**: `SensorDeploymentStation<F extends EnvironmentFamily>`
- **Factory Method Hook**: `protected abstract MonitoringSensor<F> createSensor(String sensorId, double initialCalibrationOffset);`
- **Template Business Logic**: `public final DeploymentReport<F> deployAndCalibrateSensor(...)`
  - Validates sensor ID and safety envelopes.
  - Calls `createSensor(...)` polymorphically.
  - Executes calibration routine on hardware.
  - Triggers internal diagnostic self-test audit.
  - Verifies baseline telemetry.
  - Computes a SHA-256 cryptographic hardware verification checksum.
- **Why is this a TRUE Factory Method rather than a static factory?**
  1. *Polymorphic Hook*: Instantiation is deferred to subclasses (`EnterpriseSensorDeployer`, `EdgeSensorDeployer`, etc.). A static factory cannot be overridden by subclasses.
  2. *Inversion of Control (IoC)*: The Creator contains high-level business orchestration relying on an abstract product interface, following the Hollywood Principle ("Don't call us, we'll call you").
  3. *Open/Closed Principle*: Introducing new sensors requires only subclassing `SensorDeploymentStation`, without touching existing deployment pipelines.

### Part C & D: Abstract Factory & Compile-Time Compatibility Rule
- **Abstract Factory Interface**: `InfrastructureFactory<F extends EnvironmentFamily>`
  - `CoolingUnit<F> createCoolingUnit(String unitId);`
  - `PowerSupply<F> createPowerSupply(String unitId);`
  - `MonitoringSensor<F> createMonitoringSensor(String unitId);`
- **Mandatory Compatibility Rule Enforcement**:
  - Instead of relying on runtime checks (`if (family != other) throw ...`), the system enforces family safety **at compile time** using Java generics bounded by `F extends EnvironmentFamily`.
  - Collaborating methods are strongly typed:
    ```java
    coolingUnit.coolPowerSupply(PowerSupply<F> powerSupply);
    powerSupply.switchSourceToBackup(MonitoringSensor<F> sensor);
    sensor.readTelemetry(PowerSupply<F> powerSupply);
    ```
  - Attempting to pass `PowerSupply<EdgeFamily>` to `CoolingUnit<EnterpriseFamily>` produces a **Java compile-time type mismatch error**. Cross-family contamination is impossible at the language type level!

### Part E: Runtime Factory Selection (`FactoryProvider`)
- `FactoryProvider` maintains a thread-safe registry of factory suppliers.
- Dynamically resolves factories based on:
  1. Explicit identifier (`FactoryProvider.getFactory("ENTERPRISE")`)
  2. `config.properties` on classpath
  3. System Property (`-Dinfrastructure.family=EDGE`)
  4. Environment Variable (`INFRASTRUCTURE_FAMILY=EXTREME`)
- Throws `UnknownFamilyException` for unknown keys and `ConfigurationException` for missing config files.

### Part F: Multi-Product Collaborative Business Operations
Implemented in `InfrastructureOrchestrator<F>`:
1. **Operation 1: Emergency Thermal Regulation & Power Throttling**:
   Sensor detects thermal surge -> Orchestrator derates PowerSupply output by 40% -> Computes new thermal waste -> Coordinates CoolingUnit to initiate emergency heat dissipation and cool the PowerSupply directly -> Sensor verifies thermal runaway averted.
2. **Operation 2: Routine Health & Efficiency Telemetry Audit**:
   Sensor executes self-test diagnostic audit -> Samples integrated telemetry from PowerSupply -> Calculates theoretical vs actual heat dissipation -> CoolingUnit activates matching coolant circulation -> Validates cooling capacity margins -> Compiles `AuditTelemetryReport`.
3. **Operation 3: Power Grid Failover & Load Shedding Sequence**:
   Grid incident detected -> PowerSupply switches to backup battery/generator coordinating with Sensor -> Non-essential workloads shed -> CoolingUnit throttles to match reduced heat envelope -> Compiles `FailoverReport`.

### Part G: 4th Product Family Extension (OCP Audit)
- **Added 4th Family**: Orbital Space Station (`SpaceStationFamily`, `SpaceStationCoolingUnit`, `SpaceStationPowerSupply`, `SpaceStationSensor`, `SpaceStationInfrastructureFactory`, `SpaceStationSensorDeployer`).
- **Open/Closed Principle Audit**:
  - **Files Added (7)**:
    1. `com/infrastructure/family/SpaceStationFamily.java`
    2. `com/infrastructure/products/concrete/space/SpaceStationCoolingUnit.java`
    3. `com/infrastructure/products/concrete/space/SpaceStationPowerSupply.java`
    4. `com/infrastructure/products/concrete/space/SpaceStationSensor.java`
    5. `com/infrastructure/factorymethod/SpaceStationSensorDeployer.java`
    6. `com/infrastructure/abstractfactory/SpaceStationInfrastructureFactory.java`
    7. `src/test/java/com/infrastructure/SpaceStationExtensionTest.java`
  - **Files Modified (1)**:
    1. `com/infrastructure/config/FactoryProvider.java` (registered `"SPACE_STATION"` supplier in static block).
  - **Files Untouched**:
    All product interfaces, abstract creator classes, existing concrete factories, `InfrastructureOrchestrator`, and `InfrastructureManagementApplication`.

---

## 4. Complete UML Class Diagram (Mermaid)

```mermaid
classDiagram
    direction TB

    %% =========================================================================
    %% CLIENT & ORCHESTRATOR
    %% =========================================================================
    class InfrastructureManagementApplication {
        +main(args: String[]) void
    }

    class InfrastructureOrchestrator~F~ {
        -coolingUnit: CoolingUnit~F~
        -powerSupply: PowerSupply~F~
        -sensor: MonitoringSensor~F~
        -factory: InfrastructureFactory~F~
        -zoneId: String
        +executeEmergencyThermalRegulation(simulatedSpike: double) EmergencyThrottlingReport
        +performRoutineHealthAudit() AuditTelemetryReport
        +executeFailoverSequence(incidentDesc: String) FailoverReport
    }

    InfrastructureManagementApplication ..> FactoryProvider : queries
    InfrastructureManagementApplication ..> InfrastructureOrchestrator : creates & runs
    InfrastructureOrchestrator --> InfrastructureFactory : uses
    InfrastructureOrchestrator --> CoolingUnit : coordinates
    InfrastructureOrchestrator --> PowerSupply : coordinates
    InfrastructureOrchestrator --> MonitoringSensor : coordinates

    %% =========================================================================
    %% PRODUCT FAMILY HIERARCHY
    %% =========================================================================
    class EnvironmentFamily {
        <<interface>>
        +getFamilyCode() String
        +getDescription() String
    }
    class EnterpriseFamily { +INSTANCE }
    class EdgeFamily { +INSTANCE }
    class ExtremeFamily { +INSTANCE }
    class SpaceStationFamily { +INSTANCE }

    EnvironmentFamily <|.. EnterpriseFamily
    EnvironmentFamily <|.. EdgeFamily
    EnvironmentFamily <|.. ExtremeFamily
    EnvironmentFamily <|.. SpaceStationFamily

    %% =========================================================================
    %% PRODUCT INTERFACES & CONCRETE PRODUCTS
    %% =========================================================================
    class CoolingUnit~F~ {
        <<interface>>
        +getModelName() String
        +getCurrentCoolingLoadKw() double
        +getMaxCapacityKw() double
        +getStatus() CoolingStatus
        +activateCooling(heatOutputKw: double) void
        +emergencyHeatDissipation(spikeKw: double) void
        +coolPowerSupply(powerSupply: PowerSupply~F~) void
        +shutdown() void
    }

    class PowerSupply~F~ {
        <<interface>>
        +getModelName() String
        +getSuppliedWattageKw() double
        +getMaxCapacityKw() double
        +getEfficiencyRating() double
        +getStatus() PowerStatus
        +adjustOutput(requiredKw: double) void
        +switchSourceToBackup(sensor: MonitoringSensor~F~) void
        +triggerEmergencyShutdown() void
        +calculateHeatDissipationKw() double
    }

    class MonitoringSensor~F~ {
        <<interface>>
        +getSensorId() String
        +getStatus() SensorStatus
        +readTelemetry() TelemetryReading
        +readTelemetry(powerSupply: PowerSupply~F~) TelemetryReading
        +calibrate(tolerance: double) void
        +isThresholdExceeded() boolean
        +triggerDiagnosticAudit() void
    }

    %% Concrete Products - Enterprise
    class EnterpriseCoolingUnit { -loopId: String }
    class EnterprisePowerSupply { -powerBankId: String }
    class EnterpriseSensor { -sensorId: String }
    CoolingUnit <|.. EnterpriseCoolingUnit
    PowerSupply <|.. EnterprisePowerSupply
    MonitoringSensor <|.. EnterpriseSensor

    %% Concrete Products - Edge
    class EdgeCoolingUnit { -fanArrayId: String }
    class EdgePowerSupply { -busSerial: String }
    class EdgeSensor { -probeId: String }
    CoolingUnit <|.. EdgeCoolingUnit
    PowerSupply <|.. EdgePowerSupply
    MonitoringSensor <|.. EdgeSensor

    %% Concrete Products - Extreme
    class ExtremeCoolingUnit { -bathSerial: String }
    class ExtremePowerSupply { -rtgUnitId: String }
    class ExtremeSensor { -hardenedProbeId: String }
    CoolingUnit <|.. ExtremeCoolingUnit
    PowerSupply <|.. ExtremePowerSupply
    MonitoringSensor <|.. ExtremeSensor

    %% Concrete Products - Space Station
    class SpaceStationCoolingUnit { -radiatorId: String }
    class SpaceStationPowerSupply { -powerArrayId: String }
    class SpaceStationSensor { -telemetryModuleId: String }
    CoolingUnit <|.. SpaceStationCoolingUnit
    PowerSupply <|.. SpaceStationPowerSupply
    MonitoringSensor <|.. SpaceStationSensor

    %% =========================================================================
    %% SUBGRAPH: FACTORY METHOD PATTERN
    %% =========================================================================
    subgraph Factory_Method_Pattern ["Factory Method Pattern (Sensor Deployment)"]
        class SensorDeploymentStation~F~ {
            <<abstract>>
            -family: F
            +deployAndCalibrateSensor(sensorId: String, offset: double) DeploymentReport~F~
            #createSensor(sensorId: String, offset: double)* MonitoringSensor~F~
        }
        class EnterpriseSensorDeployer {
            #createSensor(sensorId: String, offset: double) MonitoringSensor~EnterpriseFamily~
        }
        class EdgeSensorDeployer {
            #createSensor(sensorId: String, offset: double) MonitoringSensor~EdgeFamily~
        }
        class ExtremeSensorDeployer {
            #createSensor(sensorId: String, offset: double) MonitoringSensor~ExtremeFamily~
        }
        class SpaceStationSensorDeployer {
            #createSensor(sensorId: String, offset: double) MonitoringSensor~SpaceStationFamily~
        }

        SensorDeploymentStation <|-- EnterpriseSensorDeployer
        SensorDeploymentStation <|-- EdgeSensorDeployer
        SensorDeploymentStation <|-- ExtremeSensorDeployer
        SensorDeploymentStation <|-- SpaceStationSensorDeployer

        EnterpriseSensorDeployer ..> EnterpriseSensor : creates
        EdgeSensorDeployer ..> EdgeSensor : creates
        ExtremeSensorDeployer ..> ExtremeSensor : creates
        SpaceStationSensorDeployer ..> SpaceStationSensor : creates
    end

    %% =========================================================================
    %% SUBGRAPH: ABSTRACT FACTORY PATTERN
    %% =========================================================================
    subgraph Abstract_Factory_Pattern ["Abstract Factory Pattern (Infrastructure Suite)"]
        class InfrastructureFactory~F~ {
            <<interface>>
            +createCoolingUnit(unitId: String) CoolingUnit~F~
            +createPowerSupply(unitId: String) PowerSupply~F~
            +createMonitoringSensor(unitId: String) MonitoringSensor~F~
            +getFamily() F
            +getFamilyIdentifier() String
        }

        class EnterpriseInfrastructureFactory {
            +createCoolingUnit(unitId: String) CoolingUnit~EnterpriseFamily~
            +createPowerSupply(unitId: String) PowerSupply~EnterpriseFamily~
            +createMonitoringSensor(unitId: String) MonitoringSensor~EnterpriseFamily~
        }
        class EdgeInfrastructureFactory {
            +createCoolingUnit(unitId: String) CoolingUnit~EdgeFamily~
            +createPowerSupply(unitId: String) PowerSupply~EdgeFamily~
            +createMonitoringSensor(unitId: String) MonitoringSensor~EdgeFamily~
        }
        class ExtremeInfrastructureFactory {
            +createCoolingUnit(unitId: String) CoolingUnit~ExtremeFamily~
            +createPowerSupply(unitId: String) PowerSupply~ExtremeFamily~
            +createMonitoringSensor(unitId: String) MonitoringSensor~ExtremeFamily~
        }
        class SpaceStationInfrastructureFactory {
            +createCoolingUnit(unitId: String) CoolingUnit~SpaceStationFamily~
            +createPowerSupply(unitId: String) PowerSupply~SpaceStationFamily~
            +createMonitoringSensor(unitId: String) MonitoringSensor~SpaceStationFamily~
        }

        InfrastructureFactory <|.. EnterpriseInfrastructureFactory
        InfrastructureFactory <|.. EdgeInfrastructureFactory
        InfrastructureFactory <|.. ExtremeInfrastructureFactory
        InfrastructureFactory <|.. SpaceStationInfrastructureFactory

        EnterpriseInfrastructureFactory ..> EnterpriseCoolingUnit : creates
        EnterpriseInfrastructureFactory ..> EnterprisePowerSupply : creates
        EnterpriseInfrastructureFactory ..> EnterpriseSensor : creates

        EdgeInfrastructureFactory ..> EdgeCoolingUnit : creates
        EdgeInfrastructureFactory ..> EdgePowerSupply : creates
        EdgeInfrastructureFactory ..> EdgeSensor : creates

        ExtremeInfrastructureFactory ..> ExtremeCoolingUnit : creates
        ExtremeInfrastructureFactory ..> ExtremePowerSupply : creates
        ExtremeInfrastructureFactory ..> ExtremeSensor : creates

        SpaceStationInfrastructureFactory ..> SpaceStationCoolingUnit : creates
        SpaceStationInfrastructureFactory ..> SpaceStationPowerSupply : creates
        SpaceStationInfrastructureFactory ..> SpaceStationSensor : creates
    end

    %% =========================================================================
    %% FACTORY PROVIDER & RUNTIME SELECTION
    %% =========================================================================
    class FactoryProvider {
        -REGISTRY: Map~String, Supplier~
        +registerFactory(familyId: String, supplier: Supplier) void
        +getFactory(familyIdentifier: String) InfrastructureFactory~?~
        +getFactoryFromConfig() InfrastructureFactory~?~
    }

    FactoryProvider ..> InfrastructureFactory : resolves
    FactoryProvider ..> EnterpriseInfrastructureFactory : instantiates
    FactoryProvider ..> EdgeInfrastructureFactory : instantiates
    FactoryProvider ..> ExtremeInfrastructureFactory : instantiates
    FactoryProvider ..> SpaceStationInfrastructureFactory : instantiates
```

---

## 5. Automated Test Suite (26 JUnit 5 Tests)

To run the automated test suite:
```bash
mvn clean test
```

### Test Coverage Breakdown

| Category | Test Count | Description |
| :--- | :---: | :--- |
| **Original Family Creation** | 3 | Verifies `EnterpriseInfrastructureFactory`, `EdgeInfrastructureFactory`, and `ExtremeInfrastructureFactory` instantiate correct concrete types. |
| **Concrete Product Behaviors** | 3 | Tests specialized behaviors (CRAH glycol flow, PWM fan speed, RTG containment, radiation flux). |
| **Factory Method Lifecycle** | 4 | Verifies `SensorDeploymentStation` execution, self-test verification, SHA-256 checksums, and negative parameter handling. |
| **Runtime Factory Selection** | 5 | Tests dynamic resolution by string identifier, classpath `config.properties`, and negative error scenarios (`UnknownFamilyException`, `ConfigurationException`). |
| **Multi-Product Business Operations** | 3 | Tests Emergency Thermal Regulation, Routine Health Audit, and Grid Failover across products. |
| **Client Abstraction & Compatibility** | 2 | Proves `InfrastructureOrchestrator` runs without concrete type knowledge and verifies compile-time type safety. |
| **4th Product Family (Space Station)** | 4 | Verifies extension creation, operational state, Factory Method deployer, and business scenarios on the new family. |
| **Total Tests** | **26** | **100% Pass Rate** |

---

## 6. Git History & Meaningful Commit Plan (8 Commits)

```
* c499132 feat(extension): add fourth product family (space station) demonstrating open-closed principle
* 2bea708 test: add comprehensive junit5 test suite for creation, compatibility, and business operations
* 2c1a7d2 feat(orchestrator): implement multi-product collaboration business scenarios and client application
* 1171829 feat(config): add runtime factory selection provider and external configuration loader
* 7f925d8 feat(abstract-factory): implement infrastructure factory and initial three concrete product families
* 795112b feat(factory-method): introduce sensor deployment station creator and concrete deployers
* 855d994 feat(products): introduce domain models and generic family-bounded product abstractions
* 6598c15 feat(legacy): implement initial domain model and legacy client with direct instantiation anti-patterns
```

### Exact Git Staging and Commit Commands

1. **Commit 1 (Part A)**:
   ```bash
   git add pom.xml .gitignore src/main/java/com/infrastructure/legacy/
   git commit -m "feat(legacy): implement initial domain model and legacy client with direct instantiation anti-patterns"
   ```
2. **Commit 2 (Products & Families)**:
   ```bash
   git add src/main/java/com/infrastructure/family/ src/main/java/com/infrastructure/model/ src/main/java/com/infrastructure/products/
   git commit -m "feat(products): introduce domain models and generic family-bounded product abstractions"
   ```
3. **Commit 3 (Part B: Factory Method)**:
   ```bash
   git add src/main/java/com/infrastructure/products/concrete/ src/main/java/com/infrastructure/factorymethod/
   git commit -m "feat(factory-method): introduce sensor deployment station creator and concrete deployers"
   ```
4. **Commit 4 (Part C & D: Abstract Factory)**:
   ```bash
   git add src/main/java/com/infrastructure/abstractfactory/
   git commit -m "feat(abstract-factory): implement infrastructure factory and initial three concrete product families"
   ```
5. **Commit 5 (Part E: Runtime Selection)**:
   ```bash
   git add src/main/resources/config.properties src/main/java/com/infrastructure/config/
   git commit -m "feat(config): add runtime factory selection provider and external configuration loader"
   ```
6. **Commit 6 (Part F: Business Operations & Client)**:
   ```bash
   git add src/main/java/com/infrastructure/service/ src/main/java/com/infrastructure/client/
   git commit -m "feat(orchestrator): implement multi-product collaboration business scenarios and client application"
   ```
7. **Commit 7 (Part I: Tests)**:
   ```bash
   git add src/main/java/com/infrastructure/products/concrete/ src/test/java/com/infrastructure/InfrastructureSystemTest.java
   git commit -m "test: add comprehensive junit5 test suite for creation, compatibility, and business operations"
   ```
8. **Commit 8 (Part G: 4th Family Extension)**:
   ```bash
   git add src/main/java/com/infrastructure/family/SpaceStationFamily.java \
           src/main/java/com/infrastructure/products/concrete/space/ \
           src/main/java/com/infrastructure/factorymethod/SpaceStationSensorDeployer.java \
           src/main/java/com/infrastructure/abstractfactory/SpaceStationInfrastructureFactory.java \
           src/main/java/com/infrastructure/config/FactoryProvider.java \
           src/test/java/com/infrastructure/SpaceStationExtensionTest.java
   git commit -m "feat(extension): add fourth product family (space station) demonstrating open-closed principle"
   ```

---

## 7. How to Build and Run

### Run All Automated Tests:
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
mvn clean test
```

### Run Demonstration Client Application:
```bash
mvn compile
java -cp target/classes com.infrastructure.client.InfrastructureManagementApplication
```

### Run with Dynamic Family Override:
```bash
# Pass family name as CLI parameter:
java -cp target/classes com.infrastructure.client.InfrastructureManagementApplication EDGE
java -cp target/classes com.infrastructure.client.InfrastructureManagementApplication EXTREME
java -cp target/classes com.infrastructure.client.InfrastructureManagementApplication SPACE_STATION

# Or pass via Java System Property:
java -Dinfrastructure.family=SPACE_STATION -cp target/classes com.infrastructure.client.InfrastructureManagementApplication
```
