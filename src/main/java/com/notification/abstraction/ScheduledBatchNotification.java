package com.notification.abstraction;

import com.notification.implementor.MessageSender;
import com.notification.model.BatchReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Refined Abstraction: Scheduled Batch Notification.
 * <p>
 * Business rules:
 * 1. Maintains a collection of recipients to receive a broadcast or scheduled message.
 * 2. Iterates over recipients and dispatches through the bridged {@link MessageSender}.
 * 3. Aggregates results and produces a clear summary report with Total, Successful, and Failed metrics.
 */
public class ScheduledBatchNotification extends Notification {

    private final List<String> recipients = new ArrayList<>();
    private BatchReport lastReport;

    public ScheduledBatchNotification(MessageSender sender) {
        super(sender);
    }

    public ScheduledBatchNotification(MessageSender sender, List<String> recipients) {
        super(sender);
        if (recipients != null) {
            this.recipients.addAll(recipients);
        }
    }

    public void addRecipient(String recipient) {
        if (recipient != null && !recipient.isBlank()) {
            this.recipients.add(recipient.trim());
        }
    }

    public void setRecipients(List<String> recipients) {
        this.recipients.clear();
        if (recipients != null) {
            for (String r : recipients) {
                if (r != null && !r.isBlank()) {
                    this.recipients.add(r.trim());
                }
            }
        }
    }

    public List<String> getRecipients() {
        return Collections.unmodifiableList(recipients);
    }

    @Override
    public void send(String recipient, String message) {
        sendBatch(List.of(recipient), message);
    }

    /**
     * Sends the message to all configured recipients in the batch.
     *
     * @param message Text content to broadcast.
     * @return BatchReport with total, successful, and failed counters.
     */
    public BatchReport sendBatch(String message) {
        return sendBatch(new ArrayList<>(this.recipients), message);
    }

    /**
     * Sends the message to an explicit list of recipients.
     *
     * @param targetRecipients Recipient collection.
     * @param message          Text content to broadcast.
     * @return BatchReport with delivery counters.
     */
    public BatchReport sendBatch(List<String> targetRecipients, String message) {
        if (targetRecipients == null) {
            targetRecipients = Collections.emptyList();
        }

        int total = targetRecipients.size();
        int successful = 0;
        int failed = 0;

        String title = "Scheduled Broadcast";

        for (String recipient : targetRecipients) {
            boolean delivered = false;
            if (sender != null && sender.isAvailable()) {
                delivered = sender.sendMessage(recipient, title, message);
            }
            if (delivered) {
                successful++;
            } else {
                failed++;
            }
        }

        this.lastReport = new BatchReport(total, successful, failed);
        printSummary(lastReport);
        return lastReport;
    }

    private void printSummary(BatchReport report) {
        System.out.println("Total: " + report.total());
        System.out.println("Successful: " + report.successful());
        System.out.println("Failed: " + report.failed());
    }

    public BatchReport getLastReport() {
        return lastReport;
    }
}
