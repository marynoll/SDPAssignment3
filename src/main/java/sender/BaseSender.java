package sender;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSender implements MessageSender {

    private boolean available = true;
    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }

    protected boolean deliver(String text) {
        sentMessages.add(text);
        System.out.println("   [" + getChannelName() + "] " + text);
        return true;
    }
}