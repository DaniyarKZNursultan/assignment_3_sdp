package com.notification.implementor;

/**
 * Common base class for concrete message sender implementations.
 * Encapsulates availability state tracking and message history auditing for testing and simulation.
 */
public abstract class AbstractMessageSender implements MessageSender {

    private boolean available = true;
    private String lastRecipient;
    private String lastTitle;
    private String lastBody;
    private int sentCount = 0;

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Records telemetry of the sent message for unit test assertions and audit trails.
     */
    protected void recordSent(String recipient, String title, String body) {
        this.lastRecipient = recipient;
        this.lastTitle = title;
        this.lastBody = body;
        this.sentCount++;
    }

    public String getLastRecipient() {
        return lastRecipient;
    }

    public String getLastTitle() {
        return lastTitle;
    }

    public String getLastBody() {
        return lastBody;
    }

    public int getSentCount() {
        return sentCount;
    }

    public void resetHistory() {
        this.lastRecipient = null;
        this.lastTitle = null;
        this.lastBody = null;
        this.sentCount = 0;
    }
}
