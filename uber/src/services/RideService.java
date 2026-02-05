package uber.src.services;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import uber.src.models.Driver;
import uber.src.models.Location;
import uber.src.models.Ride;
import uber.src.models.RideRequest;
import uber.src.models.Rider;
import uber.src.repositories.IRideRepository;

public class RideService implements IRideService {
    private final IRideRepository rideRepository;
    private final BlockingQueue<RideRequest> rideRequestQueue;

    private static final int ASSIGNMENT_TIMEOUT_SECONDS = 60;

    public RideService(IRideRepository rideRepository, BlockingQueue<RideRequest> rideRequestQueue) {
        this.rideRepository = rideRepository;
        this.rideRequestQueue = rideRequestQueue;
    }

    // This method is blocking, means only one ride request will be processed at a
    // time.
    // We can make it non-blocking by using CompletableFuture or by using a message
    // queue like Kafka.
    @Override
    public Optional<Ride> requestRide(Rider rider, Location pickupLocation, Location dropLocation) {
        Ride ride = new Ride(rider, pickupLocation, dropLocation);
        rideRepository.addRide(ride);

        RideRequest rideRequest = new RideRequest(ride);
        try {
            boolean added = rideRequestQueue.offer(rideRequest);
            if (!added) {
                System.out.println("Failed to add ride request to the queue for ride: " + ride.getId());
                return Optional.empty();
            }

            System.out.println("Ride request added to the queue for ride: " + ride.getId());

            Driver driver = rideRequest.getResultFuture().get(ASSIGNMENT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (driver != null) {
                ride.assignDriver(driver);
                return Optional.of(ride);
            }
            return Optional.empty();
        } catch (Exception e) {
            System.out.println("Error while processing ride request for ride: " + ride.getId());
            return Optional.empty();
        }

    }

    @Override
    public RideRequest requestRideAsync(Rider rider, Location pickupLocation, Location dropLocation) {
        Ride ride = new Ride(rider, pickupLocation, dropLocation);
        rideRepository.addRide(ride);

        RideRequest rideRequest = new RideRequest(ride);
        boolean added = rideRequestQueue.offer(rideRequest);
        if (!added) {
            System.out.println("Failed to add ride request to the queue for ride: " + ride.getId());
        } else {
            System.out.println("Ride request added to the queue for ride: " + ride.getId());
        }
        return rideRequest;
    }

    @Override
    public void completeRide(String rideId) {
        Ride ride = rideRepository.getRideById(rideId);
        if (ride != null) {
            Driver driver = ride.getDriver();
            if (driver != null) {
                driver.completeRide();
            }
            ride.completeRide();
        }
    }
}
