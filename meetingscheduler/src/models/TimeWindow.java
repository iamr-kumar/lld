package meetingscheduler.src.models;

import java.time.LocalDateTime;
import java.time.Duration;

public class TimeWindow {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TimeWindow(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public boolean overlapsWith(TimeWindow other) {
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }

    public void updateStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void updateEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getDurationInMinutes() {
        return (int) Duration.between(startTime, endTime).toMinutes();
    }
}
