package loggingsystem.src.core;

public enum LogLevel {
    DEBUG(1),
    INFO(2),
    WARN(3),
    ERROR(4);

    private final int value;

    LogLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isEqual(LogLevel other) {
        return this.value == other.value;
    }

}
