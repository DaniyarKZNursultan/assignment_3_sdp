package com.infrastructure.factorymethod;

import com.infrastructure.family.EnvironmentFamily;
import com.infrastructure.model.DeploymentReport;
import com.infrastructure.model.SensorStatus;
import com.infrastructure.model.TelemetryReading;
import com.infrastructure.products.MonitoringSensor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

/**
 * =========================================================================================
 * PART B: FACTORY METHOD PATTERN — ABSTRACT CREATOR
 * =========================================================================================
 *
 * ARCHITECTURAL JUSTIFICATION: Why is this a TRUE Factory Method, NOT a Static Factory?
 * -----------------------------------------------------------------------------------------
 * 1. POLYMORPHIC INSTANTIATION HOOK:
 *    According to the Gang of Four (GoF), the Factory Method pattern defines an interface for
 *    creating an object, but defers instantiation to subclasses. Here, {@link #createSensor}
 *    is an abstract method overridden in concrete deployers (Enterprise, Edge, Extreme, Space).
 *    A static factory is merely a static helper method with branching logic; it does not allow
 *    subclasses to alter the instantiated product polymorphically.
 *
 * 2. TEMPLATE METHOD SYNERGY & INVERSION OF CONTROL:
 *    This creator does NOT simply call 'new Product()'. Instead, it encapsulates substantial
 *    domain business logic inside the final template method {@link #deployAndCalibrateSensor}.
 *    The creator orchestrates validation, initial hardware calibration, self-test diagnostics,
 *    telemetry verification, and security checksum generation. The high-level deployment
 *    workflow relies exclusively on the abstract {@link MonitoringSensor} returned by the
 *    factory method hook, demonstrating Hollywood Principle: "Don't call us, we'll call you."
 *
 * 3. EXTENSIBILITY (OPEN/CLOSED PRINCIPLE):
 *    New sensor types (e.g. Orbital Space sensors) are added by simply providing a new
 *    concrete Creator subclass without changing any line of the deployment and validation
 *    workflow logic implemented here.
 * =========================================================================================
 *
 * @param <F> The concrete environment family.
 */
public abstract class SensorDeploymentStation<F extends EnvironmentFamily> {

    private final F family;

    protected SensorDeploymentStation(F family) {
        this.family = family;
    }

    /**
     * THE FACTORY METHOD:
     * Deferred to subclasses. Each concrete creator decides which concrete MonitoringSensor
     * class to instantiate.
     */
    protected abstract MonitoringSensor<F> createSensor(String sensorId, double initialCalibrationOffset);

    /**
     * Meaningful business logic workflow (Creator core operation).
     * Orchestrates deployment, pre-flight checks, calibration, and cryptographic auditing.
     */
    public final DeploymentReport<F> deployAndCalibrateSensor(String sensorId, double initialCalibrationOffset) {
        // Step 1: Pre-flight input validation
        if (sensorId == null || sensorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Sensor ID cannot be null or empty.");
        }
        if (initialCalibrationOffset < -15.0 || initialCalibrationOffset > 15.0) {
            throw new IllegalArgumentException("Calibration offset " + initialCalibrationOffset +
                    " exceeds permissible safety envelope [-15.0, +15.0].");
        }

        // Step 2: Invoke the Factory Method to obtain the concrete product
        MonitoringSensor<F> sensor = createSensor(sensorId.trim(), initialCalibrationOffset);

        // Step 3: Execute hardware calibration routine
        sensor.calibrate(initialCalibrationOffset);

        // Step 4: Run internal diagnostic self-test
        sensor.triggerDiagnosticAudit();

        // Step 5: Read initial baseline telemetry
        TelemetryReading baseline = sensor.readTelemetry();
        if (baseline.status() == SensorStatus.CRITICAL_BREACH || baseline.status() == SensorStatus.FAULT_OFFLINE) {
            throw new IllegalStateException("Sensor " + sensorId + " failed initial post-deployment validation: " + baseline.status());
        }

        // Step 6: Generate cryptographic hardware verification checksum
        String checksum = generateChecksum(sensor.getSensorId(), family.getFamilyCode(), baseline.temperatureCelsius());

        // Step 7: Compile and return certified DeploymentReport
        String auditLog = String.format("Certified deployment of %s in %s. Status: %s. Baseline Temp: %.2fC. Checksum: %s",
                sensor.getSensorId(), family.getDescription(), sensor.getStatus(), baseline.temperatureCelsius(), checksum);

        return new DeploymentReport<>(
                sensor.getSensorId(),
                family,
                Instant.now(),
                initialCalibrationOffset,
                true,
                checksum,
                auditLog
        );
    }

    private String generateChecksum(String id, String familyCode, double baselineTemp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String payload = id + ":" + familyCode + ":" + baselineTemp;
            byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash).substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            return "CHECKSUM-OFFLINE-" + System.currentTimeMillis();
        }
    }

    public F getFamily() {
        return family;
    }
}
