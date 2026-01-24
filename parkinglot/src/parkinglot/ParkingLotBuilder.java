package parkinglot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import parkinglot.vehicle.VehicleType;

public class ParkingLotBuilder {
    private List<ParkingFloor> floors;

    public ParkingLotBuilder() {
        this.floors = new ArrayList<>();
    }

    public ParkingLotBuilder addFloor(ParkingFloor floor) {
        this.floors.add(floor);
        return this;
    }

    public ParkingLotBuilder createFloor(int floorNumber, int numOfCarSpots, int numOfBikeSpots,
            int... otherSpotCounts) {
        List<ParkingSpot> parkingSpots = new ArrayList<>();
        for (int i = 0; i < numOfCarSpots; ++i) {
            String uuid = UUID.randomUUID().toString();
            parkingSpots.add(new ParkingSpot(uuid, VehicleType.CAR));
        }
        for (int i = 0; i < numOfBikeSpots; ++i) {
            String uuid = UUID.randomUUID().toString();
            parkingSpots.add(new ParkingSpot(uuid, VehicleType.BIKE));
        }
        if (otherSpotCounts != null) {
            for (int i = 0; i < otherSpotCounts.length; ++i) {
                String uuid = UUID.randomUUID().toString();
                parkingSpots.add(new ParkingSpot(uuid, VehicleType.OTHERS));
            }
        }
        ParkingFloor floor = new ParkingFloor(floorNumber, parkingSpots);
        floors.add(floor);
        return this;
    }

    public ParkingLot build() {
        ParkingLot parkingLot = ParkingLot.getInstance();
        for (ParkingFloor floor : floors) {
            parkingLot.addFloor(floor);
        }
        return parkingLot;
    }
}
