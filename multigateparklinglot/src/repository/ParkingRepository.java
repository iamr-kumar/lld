package multigateparklinglot.src.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import multigateparklinglot.src.models.gate.Gate;
import multigateparklinglot.src.models.parking.ParkingFloor;
import multigateparklinglot.src.models.parking.ParkingSpot;

public class ParkingRepository {
    private final Map<Integer, ParkingFloor> parkingFloors;
    private final Map<String, Gate> gates;

    public ParkingRepository() {
        this.parkingFloors = new ConcurrentHashMap<>();
        this.gates = new ConcurrentHashMap<>();
    }

    public void addParkingFloor(ParkingFloor parkingFloor) {
        parkingFloors.putIfAbsent(parkingFloor.getFloorNumber(), parkingFloor);
    }

    public void addGate(Gate gate) {
        gates.putIfAbsent(gate.getGateId(), gate);
    }

    public void addSpotToParkingFloor(int floorNumber, ParkingSpot parkingSpot) {
        // use compute if present for thread safety
        // if floor number does not exist, we can ignore the request as it is invalid
        parkingFloors.computeIfPresent(floorNumber, (key, floor) -> {
            floor.addParkingSpot(parkingSpot);
            return floor;
        });
    }

    public void addNewParkingFloor(int floorNumber) {
        parkingFloors.putIfAbsent(floorNumber, new ParkingFloor(floorNumber));
    }

    public List<ParkingSpot> getAllParkingSpots() {
        List<ParkingSpot> allSpots = new ArrayList<>();
        for (ParkingFloor floor : parkingFloors.values()) {
            List<ParkingSpot> floorSpots = floor.getAllParkingSpots();
            for (ParkingSpot spot : floorSpots) {
                if (spot.isAvailable()) {
                    allSpots.add(spot);
                }
            }
        }
        return allSpots;
    }
}
