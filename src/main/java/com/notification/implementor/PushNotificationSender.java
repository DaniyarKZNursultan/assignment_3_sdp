package com.notification.implementor;

/**
 * Concrete Implementor (Added as independent extension):
 * Simulates mobile push notification delivery via APNs / Firebase Cloud Messaging (FCM).
 * Integrates seamlessly with all Notification abstractions without requiring any modifications to them.
 */
public class PushNotificationSender extends AbstractMessageSender {

    @Override
    public String getChannelName() {
        return "PushNotification";
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            System.err.println("[FCM/APNs Gateway] Delivery to device token [" + recipient + "] failed: Push Service UNAVAILABLE.");
            return false;
        }

        String pushPayload = String.format("{\"aps\":{\"alert\":{\"title\":\"%s\",\"body\":\"%s\"},\"badge\":1,\"sound\":\"default\"}}",
                escapeJson(title), escapeJson(body));

        System.out.printf("[Push Notification] [Delivered] -> Device Token: %s%nPush Payload: %s%n",
                recipient, pushPayload);

        recordSent(recipient, title, pushPayload);
        return true;
    }

    private String escapeJson(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }
}
