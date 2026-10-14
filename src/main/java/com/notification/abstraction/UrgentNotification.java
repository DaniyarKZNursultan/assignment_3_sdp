package com.notification.abstraction;

import com.notification.implementor.MessageSender;

/**
 * Refined Abstraction: Urgent Notification.
 * <p>
 * Business rules:
 * 1. Prefixes "[URGENT] " to the outgoing message body and assigns an urgent alert title.
 * 2. Checks whether the primary sender is available before sending.
 * 3. Automatically routes to a fallback {@link MessageSender} if the primary sender is unavailable.
 * 4. Strictly polymorphic: operates solely via the {@link MessageSender} interface without {@code instanceof}.
 */
public class UrgentNotification extends Notification {

    private MessageSender fallbackSender;
    private boolean lastFallbackUsed;
    private boolean lastDeliverySuccessful;

    public UrgentNotification(MessageSender sender) {
        this(sender, null);
    }

    public UrgentNotification(MessageSender sender, MessageSender fallbackSender) {
        super(sender);
        this.fallbackSender = fallbackSender;
    }

    public MessageSender getFallbackSender() {
        return fallbackSender;
    }

    public void setFallbackSender(MessageSender fallbackSender) {
        this.fallbackSender = fallbackSender;
    }

    public boolean wasFallbackUsed() {
        return lastFallbackUsed;
    }

    public boolean isLastDeliverySuccessful() {
        return lastDeliverySuccessful;
    }

    @Override
    public void send(String recipient, String message) {
        this.lastFallbackUsed = false;
        this.lastDeliverySuccessful = false;

        String urgentMessage = "[URGENT] " + message;
        String title = "[URGENT] High-Priority Alert";

        // Step 1: Check primary sender availability
        if (sender != null && sender.isAvailable()) {
            this.lastDeliverySuccessful = sender.sendMessage(recipient, title, urgentMessage);
            return;
        }

        // Step 2: Route to fallback sender if primary is unavailable
        if (fallbackSender != null && fallbackSender.isAvailable()) {
            System.out.printf("[UrgentNotification] Primary sender [%s] is unavailable. Routing to fallback [%s].%n",
                    sender != null ? sender.getChannelName() : "None",
                    fallbackSender.getChannelName());
            this.lastFallbackUsed = true;
            this.lastDeliverySuccessful = fallbackSender.sendMessage(recipient, title + " (Fallback)", urgentMessage);
            return;
        }

        // Step 3: Both primary and fallback are unavailable
        System.err.printf("[UrgentNotification] CRITICAL: Delivery failed for recipient '%s'. Neither primary nor fallback senders are available.%n", recipient);
        this.lastDeliverySuccessful = false;
    }
}
