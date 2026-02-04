package parkinglot.src;

import parkinglot.src.vehicle.Vehicle;
import parkinglot.src.vehicle.VehicleType;

/**
 * Represents a parking spot in the parking lot.
 * Each parking spot has an ID, a type of vehicle it can accommodate,
 * and a status indicating whether it is occupied or not.
 */
public class ParkingSpot {
    private final String id;
    private boolean isOccupied;
    private final VehicleType vehicleType;
    private Vehicle vehicle;

    /**
     * Constructor to create a parking spot with a specific ID and vehicle type.
     *
     * @param id          The unique identifier for the parking spot.
     * @param vehicleType The type of vehicle that can be parked in this spot.
     */
    public ParkingSpot(String id, VehicleType vehicleType) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.isOccupied = false;
        this.vehicle = null;
    }

    /**
     * Gets the ID of the parking spot.
     *
     * @return The unique identifier of the parking spot.
     */
    public String getId() {
        return id;
    }

    /**
     * Checks if the parking spot is currently occupied.
     *
     * @return true if the parking spot is occupied, false otherwise.
     */
    public boolean isOccupied() {
        return isOccupied;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public synchronized boolean parkVehicle(Vehicle vehicle) {
        if (isOccupied) {
            return false;
        }
        if (vehicle.getType() != vehicleType) {
            return false;
        }
        this.vehicle = vehicle;
        this.isOccupied = true;
        return true;
    }

    public synchronized boolean removeVehicle() {
        if (!isOccupied) {
            return false;
        }
        this.vehicle = null;
        this.isOccupied = false;
        return true;
    }
}
