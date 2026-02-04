package kafka.src.topic;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import kafka.src.message.Message;

public class Partition {
    private final String id;
    private final Map<Integer, Message> messages;
    private final AtomicInteger currentOffset = new AtomicInteger(-1);

    public Partition(String id) {
        this.id = id;
        this.messages = new ConcurrentHashMap<>();
    }

    public int appendMessage(Message message) {
        messages.putIfAbsent(message.getOffset(), message);
        return message.getOffset();
    }

    public int getNextOffset() {
        return currentOffset.incrementAndGet();
    }

    public Optional<Message> getMessageAtOffset(int offset) {
        if (!messages.containsKey(offset)) {
            return Optional.empty();
        }
        return Optional.of(messages.get(offset));
    }

    public String getId() {
        return id;
    }
}
