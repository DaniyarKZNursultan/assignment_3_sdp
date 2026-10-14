package com.notification.abstraction;

import com.notification.implementor.MessageSender;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Refined Abstraction: Encrypted Notification.
 * <p>
 * Business rules:
 * 1. Encrypts/transforms plaintext message body into Base64 ciphertext before transmission.
 * 2. Ensures data confidentiality across untrusted transport channels.
 */
public class EncryptedNotification extends Notification {

    private String lastEncryptedMessage;
    private boolean lastDeliverySuccessful;

    public EncryptedNotification(MessageSender sender) {
        super(sender);
    }

    @Override
    public void send(String recipient, String message) {
        this.lastDeliverySuccessful = false;
        this.lastEncryptedMessage = encrypt(message);

        String title = "Encrypted Transmission";

        if (sender != null && sender.isAvailable()) {
            this.lastDeliverySuccessful = sender.sendMessage(recipient, title, lastEncryptedMessage);
        } else {
            System.err.printf("[EncryptedNotification] Delivery failed: Sender [%s] is unavailable.%n",
                    sender != null ? sender.getChannelName() : "None");
            this.lastDeliverySuccessful = false;
        }
    }

    /**
     * Transforms raw plaintext into Base64 encoded ciphertext.
     */
    public static String encrypt(String plainText) {
        if (plainText == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(plainText.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes Base64 encoded ciphertext back to plaintext.
     */
    public static String decrypt(String cipherText) {
        if (cipherText == null) {
            return null;
        }
        return new String(Base64.getDecoder().decode(cipherText), StandardCharsets.UTF_8);
    }

    public String getLastEncryptedMessage() {
        return lastEncryptedMessage;
    }

    public String getLastEncryptedBody() {
        return lastEncryptedMessage;
    }

    public boolean isLastDeliverySuccessful() {
        return lastDeliverySuccessful;
    }
}
