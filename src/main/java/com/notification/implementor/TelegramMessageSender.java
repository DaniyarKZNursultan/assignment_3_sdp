package com.notification.implementor;

/**
 * Concrete Implementor: Simulates delivering formatted messages via Telegram Bot API.
 */
public class TelegramMessageSender extends AbstractMessageSender {

    @Override
    public String getChannelName() {
        return "Telegram";
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            System.err.println("[Telegram API] Delivery to @" + recipient + " failed: Bot API service is currently UNAVAILABLE (HTTP 503).");
            return false;
        }

        // Telegram format simulation with HTML tags
        String formattedPayload = String.format("<b>%s</b>%n%s", title, body);
        System.out.printf("[Telegram Sender] [200 OK] -> Chat: @%s | Formatted Message:%n%s%n", recipient, formattedPayload);

        recordSent(recipient, title, formattedPayload);
        return true;
    }
}
