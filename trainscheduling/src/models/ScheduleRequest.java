package models;

public class ScheduleRequest {
    private final Train train;
    private final TimeWindow timeWindow;

    public ScheduleRequest(Train train, TimeWindow timeWindow) {
        this.train = train;
        this.timeWindow = timeWindow;
    }

    public Train getTrain() {
        return train;
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }
}
