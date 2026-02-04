package trainscheduling.src.models;

import trainscheduling.src.enums.Status;

public class ScheduleResponse {
    private final Status status;
    private final PlatformAssignment platformAssignment;

    public ScheduleResponse(Status status, PlatformAssignment platformAssignment) {
        this.status = status;
        this.platformAssignment = platformAssignment;
    }

    public Status getStatus() {
        return status;
    }

    public PlatformAssignment getPlatformAssignment() {
        return platformAssignment;
    }
}