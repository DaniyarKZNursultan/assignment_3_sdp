package com.notification.model;

/**
 * Immutable report representing batch delivery execution metrics.
 *
 * @param total      Total number of recipients in the batch.
 * @param successful Total number of messages dispatched successfully.
 * @param failed     Total number of messages that failed delivery.
 */
public record BatchReport(int total, int successful, int failed) {

    public BatchReport {
        if (total < 0 || successful < 0 || failed < 0) {
            throw new IllegalArgumentException("Batch counters cannot be negative.");
        }
        if (successful + failed != total) {
            throw new IllegalArgumentException("Successful (" + successful + ") + Failed (" + failed + ") must equal Total (" + total + ").");
        }
    }
}
