package sender;

public class SmsMessageSender extends BaseSender {

    private final int maxLength;

    public SmsMessageSender() {
        this(160);
    }

    public SmsMessageSender(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            return false;
        }
        if (!recipient.startsWith("+")) {
            System.out.println("   [SMS] rejected: bad phone number " + recipient);
            return false;
        }
        if (body.length() > maxLength) {
            System.out.println("   [SMS] rejected: " + body.length()
                    + " characters, limit is " + maxLength);
            return false;
        }
        return deliver("to " + recipient + ": " + body);
    }

    @Override
    public String getChannelName() {
        return "SMS";
    }
}