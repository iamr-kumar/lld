package uber.src.repositories;

import uber.src.models.Ride;

public interface IRideRepository {
    public void addRide(Ride ride);

    public Ride getRideById(String rideId);
}
