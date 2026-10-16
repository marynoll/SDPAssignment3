import notification.*;
import org.junit.jupiter.api.Test;
import sender.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BridgeTests {

    private final TelegramMessageSender telegram = new TelegramMessageSender();
    private final EmailMessageSender email = new EmailMessageSender();
    private final SmsMessageSender sms = new SmsMessageSender();
    private final KafkaEventSender kafka = new KafkaEventSender();
    private final PushNotificationSender push = new PushNotificationSender();

    @Test
    void urgentNotificationIsDeliveredWithUrgentPrefix() {
        new UrgentNotification(telegram).send("@alice", "Server down");

        assertEquals(1, telegram.getSentMessages().size());
        assertTrue(telegram.getSentMessages().get(0).contains("[URGENT] Server down"));
    }

    @Test
    void urgentWithUnavailableSenderAndNoFallbackSendsNothing() {
        telegram.setAvailable(false);

        new UrgentNotification(telegram).send("@alice", "Server down");

        assertTrue(telegram.getSentMessages().isEmpty());
    }

    @Test
    void urgentFallsBackToSmsWhenTelegramIsUnavailable() {
        telegram.setAvailable(false);

        new UrgentNotification(telegram, sms).send("+77010000001", "Server down");

        assertTrue(telegram.getSentMessages().isEmpty());
        assertEquals(1, sms.getSentMessages().size());
    }

    @Test
    void secondFallbackIsUsedWhenFirstFallbackIsAlsoDown() {
        telegram.setAvailable(false);
        sms.setAvailable(false);

        new UrgentNotification(telegram, sms, kafka).send("alice", "Server down");

        assertEquals(1, kafka.getSentMessages().size());
    }

    @Test
    void smsRejectsMessagesLongerThanLimit() {
        String exactly160 = "a".repeat(160);
        String tooLong = "a".repeat(161);

        assertTrue(sms.sendMessage("+77010000001", "t", exactly160));
        assertFalse(sms.sendMessage("+77010000001", "t", tooLong));
    }

    @Test
    void smsLimitCanBeChangedTo120() {
        SmsMessageSender smallSms = new SmsMessageSender(120);

        assertFalse(smallSms.sendMessage("+77010000001", "t", "a".repeat(121)));
    }

    @Test
    void encryptedNotificationDoesNotSendPlainText() {
        new EncryptedNotification(email).send("alice@mail.com", "secret");

        String sent = email.getSentMessages().get(0);
        assertFalse(sent.contains("secret"));
        assertTrue(sent.contains(EncryptedNotification.encrypt("secret")));
        assertEquals("secret", EncryptedNotification.decrypt(EncryptedNotification.encrypt("secret")));
    }

    @Test
    void batchNotificationCountsSuccessfulAndFailed() {
        ScheduledBatchNotification batch = new ScheduledBatchNotification(sms,
                List.of("+77010000001", "bad-number", "+77010000003"));

        batch.send(null, "Meeting at 10");

        assertEquals(3, batch.getTotal());
        assertEquals(2, batch.getSuccessful());
        assertEquals(1, batch.getFailed());
    }

    @Test
    void sameNotificationObjectCanSwitchSenderAtRuntime() {
        Notification notification = new UrgentNotification(telegram);
        notification.send("@alice", "Hello");

        notification.setSender(email);
        notification.send("alice@mail.com", "Hello");

        assertEquals(1, telegram.getSentMessages().size());
        assertEquals(1, email.getSentMessages().size());
    }

    @Test
    void smsRejectsInvalidPhoneNumber() {
        assertFalse(sms.sendMessage("not-a-phone", "t", "hello"));
        assertTrue(sms.getSentMessages().isEmpty());
    }

    @Test
    void unavailableSenderReturnsFalse() {
        kafka.setAvailable(false);

        assertFalse(kafka.sendMessage("alice", "t", "b"));
    }
    @Test
    void pushSenderWorksWithExistingEncryptedNotification() {
        new EncryptedNotification(push).send("alice", "secret");

        assertEquals(1, push.getSentMessages().size());
    }
    @Test
    void marketingNotificationWorksWithExistingSenders() {
        Notification marketing = new MarketingNotification(email);
        marketing.send("alice@mail.com", "50% off");
        marketing.setSender(kafka);
        marketing.send("alice", "50% off");

        assertTrue(email.getSentMessages().get(0).contains("[PROMO] 50% off"));
        assertTrue(kafka.getSentMessages().get(0).contains("[PROMO] 50% off"));
    }
}