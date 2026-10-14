package com.notification.implementor;

import java.time.Instant;

/**
 * Concrete Implementor: Simulates publishing asynchronous event payloads to an Apache Kafka topic.
 */
public class KafkaEventSender extends AbstractMessageSender {

    private final String topic;

    public KafkaEventSender() {
        this("notification-gateway-events");
    }

    public KafkaEventSender(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    @Override
    public String getChannelName() {
        return "Kafka";
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            System.err.println("[Kafka Cluster] Publish failed: Topic '" + topic + "' leader broker is DISCONNECTED.");
            return false;
        }

        String jsonPayload = String.format("{\"timestamp\":\"%s\",\"topic\":\"%s\",\"recipient\":\"%s\",\"title\":\"%s\",\"body\":\"%s\"}",
                Instant.now(), topic, recipient, escapeJson(title), escapeJson(body));

        System.out.printf("[Kafka Producer] [ACK] -> Topic: '%s' | Key: '%s' | Offset: %d%nRecord: %s%n",
                topic, recipient, getSentCount(), jsonPayload);

        recordSent(recipient, title, jsonPayload);
        return true;
    }

    private String escapeJson(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
