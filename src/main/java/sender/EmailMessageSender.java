package sender;

public class EmailMessageSender extends BaseSender {

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            return false;
        }
        return deliver("To: " + recipient + " | Subject: " + title
                + " | " + body + " | -- Best regards, Notification Gateway");
    }

    @Override
    public String getChannelName() {
        return "Email";
    }
}