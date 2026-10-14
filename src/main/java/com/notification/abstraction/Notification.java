package com.notification.abstraction;

import com.notification.implementor.MessageSender;

/**
 * Base Abstraction in the Bridge Pattern.
 * <p>
 * Maintains a reference (the "Bridge") to an implementor of type {@link MessageSender}.
 * Subclasses (Refined Abstractions) define high-level notification variants (Urgent,
 * Encrypted, Batch, Marketing) while delegating transport mechanisms to the sender.
 */
public abstract class Notification {

    /**
     * The Bridge reference to the implementor hierarchy.
     */
    protected MessageSender sender;

    /**
     * Initializes the notification with a concrete delivery implementor.
     *
     * @param sender The message sender implementation.
     */
    protected Notification(MessageSender sender) {
        this.sender = sender;
    }

    /**
     * Allows dynamic runtime switching of the underlying message provider
     * without altering the abstraction instance or class hierarchy.
     *
     * @param sender The new message delivery provider.
     */
    public void setSender(MessageSender sender) {
        this.sender = sender;
    }

    /**
     * Returns the currently active delivery provider.
     *
     * @return active MessageSender instance.
     */
    public MessageSender getSender() {
        return sender;
    }

    /**
     * High-level business operation to deliver a notification to a recipient.
     *
     * @param recipient Target destination identifier.
     * @param message   Plaintext message content to be sent.
     */
    public abstract void send(String recipient, String message);
}
