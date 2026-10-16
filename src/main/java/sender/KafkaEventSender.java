package sender;

public class KafkaEventSender extends BaseSender {

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            return false;
        }
        return deliver("published to topic 'notifications': {key=" + recipient
                + ", title=" + title + ", body=" + body + "}");
    }

    @Override
    public String getChannelName() {
        return "Kafka";
    }
}