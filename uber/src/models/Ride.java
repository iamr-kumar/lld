package uber.src.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import uber.src.enums.RideStatus;

public class Ride {
    private final UUID id;
    private final Rider rider;
    private Driver driver;
    private final Location pickupLocation;
    private final Location dropoffLocation;
    private RideStatus status;

    private final Set<String> excludeDriverIds;

    public Ride(Rider rider, Location pickupLocation, Location dropoffLocation) {
        this.id = UUID.randomUUID();
        this.rider = rider;
        this.driver = null;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.status = RideStatus.REQUESTED;
        this.excludeDriverIds = new HashSet<>();
    }

    public void excludeDriver(String driverId) {
        excludeDriverIds.add(driverId);
    }

    public boolean isDriverExcluded(String driverId) {
        return excludeDriverIds.contains(driverId);
    }

    public Rider getRider() {
        return rider;
    }

    public String getId() {
        return id.toString();
    }

    public void assignDriver(Driver driver) {
        this.driver = driver;
        this.status = RideStatus.ACCEPTED;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDropoffLocation() {
        return dropoffLocation;
    }

    public RideStatus getStatus() {
        return status;
    }

    public Driver getDriver() {
        return driver;
    }

    public Set<String> getExcludeDriverIds() {
        return excludeDriverIds;
    }

    public void completeRide() {
        this.status = RideStatus.COMPLETED;
    }

}
