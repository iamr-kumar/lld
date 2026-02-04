package bookmyshow.src.core;

import java.util.Date;

public class SeatLock {
    private final String id;
    private final Seat seat;
    private final Show show;
    private final Integer timeoutInSeconds;
    private final Date lockTime;
    private final User user;

    public SeatLock(Seat seat, Show show, Integer timeoutInSeconds, User user) {
        this.id = java.util.UUID.randomUUID().toString(); // Using UUID for unique ID
        this.seat = seat;
        this.show = show;
        this.timeoutInSeconds = timeoutInSeconds;
        this.lockTime = new Date();
        this.user = user;
    }

    public boolean isLockExpired() {
        long currentTime = new Date().getTime();
        long lockTimeInMillis = lockTime.getTime();
        long timeoutInMillis = timeoutInSeconds * 1000L;
        return (currentTime - lockTimeInMillis) > timeoutInMillis;
    }

    public Seat getSeat() {
        return seat;
    }

    public Show getShow() {
        return show;
    }

    public Integer getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public User getUser() {
        return user;
    }

    public String getId() {
        return id;
    }
}
