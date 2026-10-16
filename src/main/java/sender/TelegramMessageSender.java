package sender;

public class TelegramMessageSender extends BaseSender {

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            return false;
        }
        return deliver("to " + recipient + " -> *" + title + "*: " + body);
    }

    @Override
    public String getChannelName() {
        return "Telegram";
    }
}