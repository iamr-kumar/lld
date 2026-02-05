package uber.src.models;

import java.util.concurrent.CompletableFuture;

public class RideRequest {
    private final Ride ride;
    private final CompletableFuture<Driver> resultFuture;

    public RideRequest(Ride ride) {
        this.ride = ride;
        this.resultFuture = new CompletableFuture<>();
    }

    public Ride getRide() {
        return ride;
    }

    public CompletableFuture<Driver> getResultFuture() {
        return resultFuture;
    }

    public void complete(Driver driver) {
        resultFuture.complete(driver);
    }

    public void fail() {
        resultFuture.complete(null);
    }
}
