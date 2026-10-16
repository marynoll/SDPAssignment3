import main.java.notification.*;
import main.java.sender.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TelegramMessageSender telegram = new TelegramMessageSender();
        EmailMessageSender email = new EmailMessageSender();
        SmsMessageSender sms = new SmsMessageSender();

        print("1. UrgentNotification - Telegram");
        Notification urgent = new UrgentNotification(telegram);
        urgent.send("@someone", "Server is down");

        print("2. EncryptedNotification - Email");
        Notification encrypted = new EncryptedNotification(email);
        encrypted.send("someone@mail.com", "Your password is 1234");

        print("3. ScheduledBatchNotification - SMS with invalid number");
        Notification batch = new ScheduledBatchNotification(sms,
                List.of("+77010000001", "+77010000002", "bad-number", "+77010000004"));
        batch.send(null, "Hi meet me at uni");

        print("4. Runtime switching: Telegram -> Email (same object)");
        Notification switching = new UrgentNotification(telegram);
        switching.send("@someone", "Deploy finished");
        switching.setSender(email);
        switching.send("someone@mail.com", "Deploy finished");

        print("5. Telegram unavailable -> fallback to SMS");
        telegram.setAvailable(false);
        Notification withFallback = new UrgentNotification(telegram, sms);
        withFallback.send("+77010000001", "Server is down");
        telegram.setAvailable(true);
    }

    private static void print(String text) {
        System.out.println();
        System.out.println("-- " + text + " --");
    }
}