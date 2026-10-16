package sender;

public class PushNotificationSender extends BaseSender {

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            return false;
        }
        return deliver("push to device of " + recipient + ": [" + title + "] " + body);
    }

    @Override
    public String getChannelName() {
        return "Push";
    }
}