package com.notification.implementor;

/**
 * Concrete Implementor: Simulates sending short messages over GSM/SMS Gateway.
 * Strictly enforces and trims messages to a standard maximum limit (default: 160 characters).
 */
public class SmsMessageSender extends AbstractMessageSender {

    public static final int DEFAULT_MAX_LENGTH = 160;
    private final int maxLength;

    public SmsMessageSender() {
        this(DEFAULT_MAX_LENGTH);
    }

    public SmsMessageSender(int maxLength) {
        if (maxLength <= 0) {
            throw new IllegalArgumentException("Maximum length must be strictly positive: " + maxLength);
        }
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public String getChannelName() {
        return "SMS";
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            System.err.println("[SMS Gateway] Delivery to " + recipient + " failed: Cellular carrier base station is OFFLINE.");
            return false;
        }

        String deliveredBody = body;
        if (deliveredBody != null && deliveredBody.length() > maxLength) {
            System.out.printf("[SMS Warning] Message exceeds %d chars (%d chars). Truncating to standard limit.%n",
                    maxLength, deliveredBody.length());
            deliveredBody = deliveredBody.substring(0, maxLength);
        }

        System.out.printf("[SMS Gateway] [GSM Sent] -> MSISDN: %s | Chars: [%d/%d]%nContent: %s%n",
                recipient, deliveredBody != null ? deliveredBody.length() : 0, maxLength, deliveredBody);

        recordSent(recipient, title, deliveredBody);
        return true;
    }
}
