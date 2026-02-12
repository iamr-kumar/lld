package multigateparklinglot.src.models.parking;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingFloor {
    private final int floorNumber;
    private final Map<String, ParkingSpot> parkingSpots;

    public ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.parkingSpots = new ConcurrentHashMap<>();
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public List<ParkingSpot> getAllParkingSpots() {
        return parkingSpots.values().stream().toList();
    }

    public void addParkingSpot(ParkingSpot parkingSpot) {
        parkingSpots.put(parkingSpot.getLotNumber(), parkingSpot);
    }
}
