package com.infrastructure.model;

import java.time.Instant;

/**
 * Immutable telemetry snapshot captured by monitoring sensors.
 */
public record TelemetryReading(
        String sensorId,
        Instant timestamp,
        double temperatureCelsius,
        double powerDrawKw,
        double coolingLoadKw,
        double auxiliaryMetric, // e.g. Humidity %, Vibration G, or Radiation mSv
        String auxiliaryMetricName,
        SensorStatus status,
        String telemetrySummary
) {
    public static TelemetryReading of(String sensorId,
                                      double temperatureCelsius,
                                      double powerDrawKw,
                                      double coolingLoadKw,
                                      double auxiliaryMetric,
                                      String auxiliaryMetricName,
                                      SensorStatus status,
                                      String summary) {
        return new TelemetryReading(
                sensorId,
                Instant.now(),
                temperatureCelsius,
                powerDrawKw,
                coolingLoadKw,
                auxiliaryMetric,
                auxiliaryMetricName,
                status,
                summary
        );
    }
}
