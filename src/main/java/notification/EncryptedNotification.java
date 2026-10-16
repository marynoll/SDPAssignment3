package notification;

import sender.MessageSender;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptedNotification extends Notification {

    public EncryptedNotification(MessageSender sender) {
        super(sender);
    }

    public static String encrypt(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String decrypt(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }

    @Override
    public void send(String recipient, String message) {
        sender.sendMessage(recipient, "Encrypted message", encrypt(message));
    }
}