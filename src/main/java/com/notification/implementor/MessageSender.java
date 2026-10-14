package com.notification.implementor;

/**
 * Implementor interface in the Bridge Pattern.
 * Defines the contract for all concrete message delivery transport providers.
 */
public interface MessageSender {

    /**
     * Sends a message to the specified recipient.
     *
     * @param recipient The target destination (e.g. phone number, email address, username, device token).
     * @param title     The subject/headline/title of the notification.
     * @param body      The textual payload/body of the notification.
     * @return true if the message was delivered successfully, false otherwise.
     */
    boolean sendMessage(String recipient, String title, String body);

    /**
     * Checks if this sender provider is currently available and operational.
     *
     * @return true if available, false if offline or degraded.
     */
    boolean isAvailable();

    /**
     * Returns the human-readable channel or provider name (e.g. "Telegram", "Email", "SMS").
     *
     * @return provider channel name.
     */
    String getChannelName();

    /**
     * Sets or toggles provider availability state (useful for simulating outages and testing fallbacks).
     *
     * @param available desired operational status.
     */
    void setAvailable(boolean available);
}
