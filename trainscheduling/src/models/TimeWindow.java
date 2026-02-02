package models;

import java.time.LocalTime;

public class TimeWindow {
    private final LocalTime startTime;
    private final LocalTime endTime;

    public TimeWindow(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean overlapsWith(TimeWindow other) {
        return this.startTime.compareTo(other.endTime) < 0 && other.startTime.compareTo(this.endTime) < 0;
    }
}
