package com.notification;

import com.notification.abstraction.EncryptedNotification;
import com.notification.abstraction.MarketingNotification;
import com.notification.abstraction.Notification;
import com.notification.abstraction.ScheduledBatchNotification;
import com.notification.abstraction.UrgentNotification;
import com.notification.implementor.EmailMessageSender;
import com.notification.implementor.KafkaEventSender;
import com.notification.implementor.MessageSender;
import com.notification.implementor.PushNotificationSender;
import com.notification.implementor.SmsMessageSender;
import com.notification.implementor.TelegramMessageSender;
import com.notification.model.BatchReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprehensive test suite verifying the Multi-Channel Notification Gateway (Bridge Pattern).
 */
class NotificationGatewayTest {

    @Nested
    @DisplayName("1. Delivery and Provider Verification Tests")
    class DeliveryTests {

        @Test
        @DisplayName("Successful delivery via Telegram formats message with HTML tags")
        void testSuccessfulDeliveryTelegram() {
            TelegramMessageSender telegram = new TelegramMessageSender();
            assertTrue(telegram.isAvailable());

            boolean result = telegram.sendMessage("alice_dev", "Alert", "Server up");
            assertTrue(result);
            assertEquals("alice_dev", telegram.getLastRecipient());
            assertEquals("Alert", telegram.getLastTitle());
            assertEquals("<b>Alert</b>\nServer up", telegram.getLastBody());
            assertEquals(1, telegram.getSentCount());
        }

        @Test
        @DisplayName("Successful delivery via Email appends subject and formal signature")
        void testSuccessfulDeliveryEmail() {
            EmailMessageSender email = new EmailMessageSender();
            assertTrue(email.isAvailable());

            boolean result = email.sendMessage("user@example.com", "Meeting Reminder", "Sprint planning at 10 AM");
            assertTrue(result);
            assertEquals("user@example.com", email.getLastRecipient());
            assertTrue(email.getLastBody().contains("Sprint planning at 10 AM"));
            assertTrue(email.getLastBody().contains(email.getSignature()));
            assertEquals(1, email.getSentCount());
        }

        @Test
        @DisplayName("Successful delivery via Kafka publishes formatted JSON event")
        void testSuccessfulDeliveryKafka() {
            KafkaEventSender kafka = new KafkaEventSender("custom-topic");
            assertTrue(kafka.isAvailable());
            assertEquals("custom-topic", kafka.getTopic());

            boolean result = kafka.sendMessage("order-service", "ORDER_CREATED", "Order #9921 placed");
            assertTrue(result);
            assertNotNull(kafka.getLastBody());
            assertTrue(kafka.getLastBody().contains("\"topic\":\"custom-topic\""));
            assertTrue(kafka.getLastBody().contains("\"recipient\":\"order-service\""));
            assertTrue(kafka.getLastBody().contains("\"body\":\"Order #9921 placed\""));
        }

        @Test
        @DisplayName("Unavailable provider rejects delivery and returns false")
        void testUnavailableProviderFails() {
            TelegramMessageSender telegram = new TelegramMessageSender();
            telegram.setAvailable(false);
            assertFalse(telegram.isAvailable());

            boolean result = telegram.sendMessage("bob", "Ping", "Hello");
            assertFalse(result);
            assertNull(telegram.getLastRecipient());
            assertEquals(0, telegram.getSentCount());
        }
    }

    @Nested
    @DisplayName("2. UrgentNotification & Fallback Mechanism Tests")
    class UrgentNotificationTests {

        @Test
        @DisplayName("UrgentNotification adds [URGENT] tag and uses primary sender when available")
        void testUrgentNotificationPrimarySuccess() {
            TelegramMessageSender telegram = new TelegramMessageSender();
            SmsMessageSender fallbackSms = new SmsMessageSender();

            UrgentNotification urgent = new UrgentNotification(telegram, fallbackSms);
            urgent.send("ops_team", "Disk space at 92%");

            assertTrue(urgent.isLastDeliverySuccessful());
            assertFalse(urgent.wasFallbackUsed());
            assertEquals(1, telegram.getSentCount());
            assertEquals(0, fallbackSms.getSentCount());
            assertTrue(telegram.getLastBody().contains("[URGENT] Disk space at 92%"));
        }

        @Test
        @DisplayName("UrgentNotification automatically falls back to secondary sender when primary is offline")
        void testUrgentNotificationFallbackActivated() {
            TelegramMessageSender telegram = new TelegramMessageSender();
            telegram.setAvailable(false); // Simulate primary downtime

            SmsMessageSender fallbackSms = new SmsMessageSender();
            assertTrue(fallbackSms.isAvailable());

            UrgentNotification urgent = new UrgentNotification(telegram, fallbackSms);
            urgent.send("+1234567890", "Payment gateway unresponsive!");

            assertTrue(urgent.isLastDeliverySuccessful());
            assertTrue(urgent.wasFallbackUsed());
            assertEquals(0, telegram.getSentCount());
            assertEquals(1, fallbackSms.getSentCount());
            assertEquals("+1234567890", fallbackSms.getLastRecipient());
            assertTrue(fallbackSms.getLastBody().startsWith("[URGENT]"));
        }

        @Test
        @DisplayName("UrgentNotification fails gracefully when both primary and fallback senders are offline")
        void testUrgentNotificationBothSendersUnavailable() {
            TelegramMessageSender telegram = new TelegramMessageSender();
            telegram.setAvailable(false);

            SmsMessageSender fallbackSms = new SmsMessageSender();
            fallbackSms.setAvailable(false);

            UrgentNotification urgent = new UrgentNotification(telegram, fallbackSms);
            urgent.send("+1234567890", "Cluster node failed!");

            assertFalse(urgent.isLastDeliverySuccessful());
            assertFalse(urgent.wasFallbackUsed());
            assertEquals(0, telegram.getSentCount());
            assertEquals(0, fallbackSms.getSentCount());
        }
    }

    @Nested
    @DisplayName("3. SMS Length Restriction Tests")
    class SmsLengthRestrictionTests {

        @Test
        @DisplayName("SMS sender trims message exceeding standard 160 character limit")
        void testSmsTrimTo160Characters() {
            SmsMessageSender sms = new SmsMessageSender();
            String longMessage = "A".repeat(200);

            boolean result = sms.sendMessage("+1-555-0199", "Alert", longMessage);
            assertTrue(result);
            assertNotNull(sms.getLastBody());
            assertEquals(160, sms.getLastBody().length());
            assertEquals("A".repeat(160), sms.getLastBody());
        }

        @Test
        @DisplayName("SMS sender does not modify messages within standard 160 character limit")
        void testSmsUnder160CharactersNotTrimmed() {
            SmsMessageSender sms = new SmsMessageSender();
            String shortMessage = "Your verification code is 482910.";

            boolean result = sms.sendMessage("+1-555-0199", "Auth", shortMessage);
            assertTrue(result);
            assertEquals(shortMessage, sms.getLastBody());
        }

        @Test
        @DisplayName("SMS sender supports customizable limit for defense modifications (e.g. 120 chars)")
        void testSmsCustomLimit120Chars() {
            SmsMessageSender sms120 = new SmsMessageSender(120);
            assertEquals(120, sms120.getMaxLength());

            String message150 = "B".repeat(150);
            sms120.sendMessage("+1-555-0199", "Promo", message150);

            assertEquals(120, sms120.getLastBody().length());
            assertEquals("B".repeat(120), sms120.getLastBody());
        }
    }

    @Nested
    @DisplayName("4. EncryptedNotification Tests")
    class EncryptedNotificationTests {

        @Test
        @DisplayName("EncryptedNotification correctly encrypts plaintext to Base64 and allows round-trip decryption")
        void testEncryptedNotificationEncoding() {
            EmailMessageSender email = new EmailMessageSender();
            EncryptedNotification encryptedNotification = new EncryptedNotification(email);

            String rawSecret = "SuperSecretDatabasePassword_2026!";
            encryptedNotification.send("dba@company.com", rawSecret);

            assertTrue(encryptedNotification.isLastDeliverySuccessful());
            String encryptedBody = encryptedNotification.getLastEncryptedBody();
            assertNotNull(encryptedBody);
            assertFalse(encryptedBody.contains(rawSecret));

            // Verify reversible Base64 decryption
            String decrypted = EncryptedNotification.decrypt(encryptedBody);
            assertEquals(rawSecret, decrypted);

            // Verify email received the encrypted payload
            assertTrue(email.getLastBody().contains(encryptedBody));
        }

        @Test
        @DisplayName("EncryptedNotification fails delivery when underlying provider is unavailable")
        void testEncryptedNotificationFailureHandling() {
            EmailMessageSender email = new EmailMessageSender();
            email.setAvailable(false);

            EncryptedNotification encryptedNotification = new EncryptedNotification(email);
            encryptedNotification.send("dba@company.com", "Plaintext confidential data");

            assertFalse(encryptedNotification.isLastDeliverySuccessful());
            assertEquals(0, email.getSentCount());
        }
    }

    @Nested
    @DisplayName("5. ScheduledBatchNotification Tests")
    class BatchNotificationTests {

        @Test
        @DisplayName("Batch notification calculates accurate Total, Successful, and Failed metrics")
        void testBatchNotificationStatistics() {
            SmsMessageSender sms = new SmsMessageSender();
            ScheduledBatchNotification batch = new ScheduledBatchNotification(sms);

            batch.addRecipient("+1-555-0001");
            batch.addRecipient("+1-555-0002");
            batch.addRecipient("+1-555-0003");

            BatchReport report = batch.sendBatch("System update tonight at midnight.");

            assertNotNull(report);
            assertEquals(3, report.total());
            assertEquals(3, report.successful());
            assertEquals(0, report.failed());
            assertEquals(3, sms.getSentCount());
            assertEquals(report, batch.getLastReport());
        }

        @Test
        @DisplayName("Batch notification correctly attributes failures when provider is unavailable")
        void testBatchNotificationProviderUnavailable() {
            SmsMessageSender sms = new SmsMessageSender();
            sms.setAvailable(false); // Entire gateway down

            ScheduledBatchNotification batch = new ScheduledBatchNotification(
                    sms,
                    List.of("+1-111", "+1-222", "+1-333", "+1-444")
            );

            BatchReport report = batch.sendBatch("Maintenance reminder");

            assertEquals(4, report.total());
            assertEquals(0, report.successful());
            assertEquals(4, report.failed());
            assertEquals(0, sms.getSentCount());
        }
    }

    @Nested
    @DisplayName("6. Runtime Provider Switching Tests")
    class RuntimeSwitchingTests {

        @Test
        @DisplayName("Notification dynamically switches delivery provider without re-instantiating abstraction")
        void testRuntimeSenderSwitching() {
            TelegramMessageSender telegram = new TelegramMessageSender();
            EmailMessageSender email = new EmailMessageSender();

            Notification notification = new UrgentNotification(telegram);
            assertSame(telegram, notification.getSender());

            // Send via Telegram
            notification.send("engineer1", "High memory consumption on Node 4");
            assertEquals(1, telegram.getSentCount());
            assertEquals(0, email.getSentCount());

            // Switch provider at runtime to Email
            notification.setSender(email);
            assertSame(email, notification.getSender());

            // Send via Email
            notification.send("engineer1@company.com", "High memory consumption on Node 4");
            assertEquals(1, telegram.getSentCount());
            assertEquals(1, email.getSentCount());
            assertTrue(email.getLastBody().contains("[URGENT] High memory consumption on Node 4"));
        }
    }

    @Nested
    @DisplayName("7. Independent Extension Tests (PushNotificationSender & MarketingNotification)")
    class IndependentExtensionTests {

        @Test
        @DisplayName("New PushNotificationSender integrates with existing UrgentNotification without modifications")
        void testNewPushNotificationSenderIntegration() {
            PushNotificationSender push = new PushNotificationSender();
            assertEquals("PushNotification", push.getChannelName());

            Notification urgentPush = new UrgentNotification(push);
            urgentPush.send("device_token_abc_123", "Emergency evacuation notice!");

            assertEquals(1, push.getSentCount());
            assertEquals("device_token_abc_123", push.getLastRecipient());
            assertTrue(push.getLastBody().contains("aps"));
            assertTrue(push.getLastBody().contains("[URGENT] Emergency evacuation notice!"));
        }

        @Test
        @DisplayName("New MarketingNotification attaches campaign metadata and works with existing Email sender")
        void testNewMarketingNotificationWithExistingEmailSender() {
            EmailMessageSender email = new EmailMessageSender();
            MarketingNotification marketing = new MarketingNotification(email, "SUMMER_SALE");

            marketing.send("customer@shop.com", "Get 30% off your next order!");

            assertTrue(marketing.isLastDeliverySuccessful());
            assertEquals(1, email.getSentCount());
            assertTrue(email.getLastTitle().contains("SUMMER_SALE"));
            assertTrue(email.getLastBody().contains("Get 30% off your next order!"));
            assertTrue(email.getLastBody().contains("Reply STOP to unsubscribe"));
        }

        @Test
        @DisplayName("New MarketingNotification combined with new PushNotificationSender demonstrates independent extension")
        void testMarketingNotificationWithPushNotificationSender() {
            PushNotificationSender push = new PushNotificationSender();
            MarketingNotification marketingPush = new MarketingNotification(push, "CYBER_MONDAY");

            marketingPush.send("device_push_token_999", "Early bird deals unlocked!");

            assertTrue(marketingPush.isLastDeliverySuccessful());
            assertEquals(1, push.getSentCount());
            assertEquals("device_push_token_999", push.getLastRecipient());
            assertTrue(push.getLastBody().contains("CYBER_MONDAY"));
            assertTrue(push.getLastBody().contains("Early bird deals unlocked!"));
        }
    }
}
