package models;

import java.time.LocalTime;

public class PlatformState {
    private final Platform platform;
    private final LocalTime nextAvailableTime;

    public PlatformState(Platform platform, LocalTime nextAvailableTime) {
        this.platform = platform;
        this.nextAvailableTime = nextAvailableTime;
    }

    public Platform getPlatform() {
        return platform;
    }

    public LocalTime getNextAvailableTime() {
        return nextAvailableTime;
    }
}
