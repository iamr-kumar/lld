package uber.src.repositories;

import java.util.concurrent.ConcurrentHashMap;

import uber.src.models.Ride;

public class RideRepository implements IRideRepository {
    private final ConcurrentHashMap<String, Ride> rides;

    public RideRepository() {
        this.rides = new ConcurrentHashMap<>();
    }

    @Override
    public void addRide(Ride ride) {
        rides.putIfAbsent(ride.getId().toString(), ride);
    }

    @Override
    public Ride getRideById(String rideId) {
        return rides.get(rideId);
    }
}
