package taskmanagementsystem.src.task;

public enum TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT;

    public static TaskPriority fromString(String priority) {
        switch (priority.toUpperCase()) {
            case "LOW":
                return LOW;
            case "MEDIUM":
                return MEDIUM;
            case "HIGH":
                return HIGH;
            case "URGENT":
                return URGENT;
            default:
                throw new IllegalArgumentException("Unknown priority: " + priority);
        }
    }
}
