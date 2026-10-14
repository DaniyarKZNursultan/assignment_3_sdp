package com.notification.abstraction;

import com.notification.implementor.MessageSender;

/**
 * Refined Abstraction (Added as independent extension):
 * Marketing / Promotional Notification.
 * <p>
 * Business rules:
 * 1. Attaches marketing campaign branding, call-to-action headers, and opt-out unsubscribe disclosures.
 * 2. Works seamlessly with any existing or future {@link MessageSender} without modifying any sender implementation.
 * 3. Demonstrates the core advantage of the Bridge Pattern: Abstractions can vary independently of Implementations.
 */
public class MarketingNotification extends Notification {

    private String campaignCode;
    private String lastPromotionalPayload;
    private boolean lastDeliverySuccessful;

    public MarketingNotification(MessageSender sender) {
        this(sender, "GENERIC_PROMO");
    }

    public MarketingNotification(MessageSender sender, String campaignCode) {
        super(sender);
        this.campaignCode = campaignCode;
    }

    public String getCampaignCode() {
        return campaignCode;
    }

    public void setCampaignCode(String campaignCode) {
        this.campaignCode = campaignCode;
    }

    public String getLastPromotionalPayload() {
        return lastPromotionalPayload;
    }

    public boolean isLastDeliverySuccessful() {
        return lastDeliverySuccessful;
    }

    @Override
    public void send(String recipient, String message) {
        this.lastDeliverySuccessful = false;

        String title = "[PROMO] Campaign " + campaignCode;
        this.lastPromotionalPayload = message + "\n\n[Campaign ID: " + campaignCode + " | Reply STOP to unsubscribe]";

        if (sender != null && sender.isAvailable()) {
            this.lastDeliverySuccessful = sender.sendMessage(recipient, title, lastPromotionalPayload);
        } else {
            System.err.printf("[MarketingNotification] Delivery failed: Provider [%s] is unavailable.%n",
                    sender != null ? sender.getChannelName() : "None");
            this.lastDeliverySuccessful = false;
        }
    }
}
