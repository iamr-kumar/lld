package uber.src.services;

import java.util.Optional;

import uber.src.models.Location;
import uber.src.models.Ride;
import uber.src.models.RideRequest;
import uber.src.models.Rider;

public interface IRideService {
    public Optional<Ride> requestRide(Rider rider, Location pickupLocation, Location dropLocation);

    public RideRequest requestRideAsync(Rider rider, Location pickupLocation, Location dropLocation);

    public void completeRide(String rideId);
}
