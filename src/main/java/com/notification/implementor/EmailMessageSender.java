package com.notification.implementor;

/**
 * Concrete Implementor: Simulates sending rich emails via SMTP with subject and formal signature.
 */
public class EmailMessageSender extends AbstractMessageSender {

    private final String signature;

    public EmailMessageSender() {
        this("--\nBest regards,\nMulti-Channel Notification Gateway System");
    }

    public EmailMessageSender(String signature) {
        this.signature = signature;
    }

    @Override
    public String getChannelName() {
        return "Email";
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            System.err.println("[SMTP Relay] Delivery to <" + recipient + "> failed: Mail server is OFFLINE (Connection refused).");
            return false;
        }

        String fullEmailBody = body + "\n\n" + signature;
        System.out.printf("[Email Sender] [SMTP Sent] -> To: <%s>%nSubject: %s%nBody:%n%s%n", recipient, title, fullEmailBody);

        recordSent(recipient, title, fullEmailBody);
        return true;
    }

    public String getSignature() {
        return signature;
    }
}
