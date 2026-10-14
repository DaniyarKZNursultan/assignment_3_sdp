package com.notification;

import com.notification.abstraction.EncryptedNotification;
import com.notification.abstraction.MarketingNotification;
import com.notification.abstraction.Notification;
import com.notification.abstraction.ScheduledBatchNotification;
import com.notification.abstraction.UrgentNotification;
import com.notification.implementor.EmailMessageSender;
import com.notification.implementor.MessageSender;
import com.notification.implementor.PushNotificationSender;
import com.notification.implementor.SmsMessageSender;
import com.notification.implementor.TelegramMessageSender;

import java.util.List;

/**
 * Main application demonstrating all 6 mandatory scenarios of the Bridge Pattern Gateway.
 */
public class Main {

    public static void main(String[] args) {
        printBanner("ASSIGNMENT 3: MULTI-CHANNEL NOTIFICATION GATEWAY (BRIDGE PATTERN)");

        // ---------------------------------------------------------------------------------
        // Scenario 1: UrgentNotification + Telegram
        // ---------------------------------------------------------------------------------
        printScenarioHeader(1, "UrgentNotification + TelegramMessageSender");
        MessageSender telegramSender = new TelegramMessageSender();
        Notification urgentTelegram = new UrgentNotification(telegramSender);
        urgentTelegram.send("devops_lead", "Database connection pool saturated (98% usage)!");

        // ---------------------------------------------------------------------------------
        // Scenario 2: EncryptedNotification + Email
        // ---------------------------------------------------------------------------------
        printScenarioHeader(2, "EncryptedNotification + EmailMessageSender");
        MessageSender emailSender = new EmailMessageSender();
        Notification encryptedEmail = new EncryptedNotification(emailSender);
        encryptedEmail.send("security-officer@university.edu", "Confidential student API token: sec_jwt_9817264817");

        // ---------------------------------------------------------------------------------
        // Scenario 3: ScheduledBatchNotification + SMS
        // ---------------------------------------------------------------------------------
        printScenarioHeader(3, "ScheduledBatchNotification + SmsMessageSender (Summary Output)");
        MessageSender smsSender = new SmsMessageSender();
        ScheduledBatchNotification batchSms = new ScheduledBatchNotification(
                smsSender,
                List.of("+1-555-0101", "+1-555-0102", "+1-555-0103")
        );
        batchSms.sendBatch("Exam schedule updated for Software Design Patterns. Please check Moodle portal.");

        // ---------------------------------------------------------------------------------
        // Scenario 4: Runtime switching: Telegram -> Email
        // ---------------------------------------------------------------------------------
        printScenarioHeader(4, "Runtime Provider Switching (Telegram -> Email)");
        MessageSender runtimeTelegram = new TelegramMessageSender();
        MessageSender runtimeEmail = new EmailMessageSender();

        Notification dynamicNotification = new UrgentNotification(runtimeTelegram);
        System.out.println(">>> Sending with initial provider: " + dynamicNotification.getSender().getChannelName());
        dynamicNotification.send("sysadmin", "Scheduled server maintenance begins in 15 minutes.");

        System.out.println("\n>>> Dynamically switching sender at runtime to: " + runtimeEmail.getChannelName() + " (without altering notification instance)");
        dynamicNotification.setSender(runtimeEmail);
        dynamicNotification.send("sysadmin", "Scheduled server maintenance begins in 15 minutes.");

        // ---------------------------------------------------------------------------------
        // Scenario 5: Telegram unavailable -> fallback to SMS
        // ---------------------------------------------------------------------------------
        printScenarioHeader(5, "Fallback Mechanism: Telegram Unavailable -> Fallback to SMS");
        MessageSender failingTelegram = new TelegramMessageSender();
        failingTelegram.setAvailable(false); // Simulate Telegram outage

        MessageSender fallbackSms = new SmsMessageSender();
        UrgentNotification resilientNotification = new UrgentNotification(failingTelegram, fallbackSms);

        System.out.println(">>> Primary Sender (" + failingTelegram.getChannelName() + ") available: " + failingTelegram.isAvailable());
        System.out.println(">>> Fallback Sender (" + fallbackSms.getChannelName() + ") available: " + fallbackSms.isAvailable());
        resilientNotification.send("+1-555-9999", "CRITICAL: Core authentication service unresponsive!");

        // ---------------------------------------------------------------------------------
        // Scenario 6: MarketingNotification + PushNotificationSender
        // ---------------------------------------------------------------------------------
        printScenarioHeader(6, "Independent Extension: MarketingNotification + PushNotificationSender");
        MessageSender pushSender = new PushNotificationSender();
        MarketingNotification marketingNotification = new MarketingNotification(pushSender, "BLACK_FRIDAY_2026");
        marketingNotification.send("apns_device_token_88ab19c3e4", "Flash Deal: 50% discount on cloud hosting subscriptions!");

        printBanner("ALL 6 SCENARIOS SUCCESSFULLY COMPLETED");
    }

    private static void printBanner(String title) {
        System.out.println("\n" + "=".repeat(85));
        System.out.println("  " + title);
        System.out.println("=".repeat(85));
    }

    private static void printScenarioHeader(int number, String description) {
        System.out.println("\n" + "-".repeat(85));
        System.out.printf("[SCENARIO %d] %s%n", number, description);
        System.out.println("-".repeat(85));
    }
}
