package notification;

import sender.MessageSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrgentNotification extends Notification {

    private final List<MessageSender> fallbacks;

    public UrgentNotification(MessageSender primary, MessageSender... fallbacks) {
        super(primary);
        this.fallbacks = new ArrayList<>(Arrays.asList(fallbacks));
    }

    public void addFallback(MessageSender fallback) {
        fallbacks.add(fallback);
    }

    @Override
    public void send(String recipient, String message) {
        String text = "[URGENT] " + message;

        List<MessageSender> candidates = new ArrayList<>();
        candidates.add(sender);
        candidates.addAll(fallbacks);

        for (MessageSender candidate : candidates) {
            if (candidate.isAvailable() && candidate.sendMessage(recipient, "URGENT", text)) {
                if (candidate != sender) {
                    System.out.println("   (fallback used: " + candidate.getChannelName() + ")");
                }
                return;
            }
            System.out.println("   (" + candidate.getChannelName() + " failed or unavailable)");
        }
        System.out.println("   URGENT message was NOT delivered by any provider!");
    }
}
