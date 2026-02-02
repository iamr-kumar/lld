package task;

public enum TaskStatus {
    UNASSIGNED,
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED;

    public static TaskStatus fromString(String status) {
        switch (status.toUpperCase()) {
            case "UNASSIGNED":
                return UNASSIGNED;
            case "NOT_STARTED":
                return NOT_STARTED;
            case "IN_PROGRESS":
                return IN_PROGRESS;
            case "COMPLETED":
                return COMPLETED;
            default:
                throw new IllegalArgumentException("Unknown status: " + status);
        }
    }
}
