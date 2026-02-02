package models;

public class PlatformAssignment {
    private final Train train;
    private final Platform platform;
    private final TimeWindow timeWindow;

    public PlatformAssignment(Train train, Platform platform, TimeWindow timeWindow) {
        this.train = train;
        this.platform = platform;
        this.timeWindow = timeWindow;
    }

    public Train getTrain() {
        return train;
    }

    public Platform getPlatform() {
        return platform;
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }
}
