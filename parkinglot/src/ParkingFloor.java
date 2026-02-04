package parkinglot.src;

import java.util.List;
import java.util.Optional;

import parkinglot.src.vehicle.VehicleType;

public class ParkingFloor {
    private int floorNumber;
    private List<ParkingSpot> parkingSpots;

    public ParkingFloor(int floorNumber, List<ParkingSpot> parkingSpots) {
        this.floorNumber = floorNumber;
        this.parkingSpots = parkingSpots;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public List<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }

    public synchronized Optional<ParkingSpot> findAvailableSpot(VehicleType type) {
        return parkingSpots.stream()
                .filter(spot -> !spot.isOccupied() && spot.getVehicleType() == type)
                .findFirst();
    }
}
