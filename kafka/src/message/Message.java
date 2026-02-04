package kafka.src.message;

public class Message {
    private final String message;
    private final int offset;

    public Message(String message, int offset) {
        this.message = message;
        this.offset = offset;
    }

    public String getMessage() {
        return message;
    }

    public int getOffset() {
        return offset;
    }
}
