package multigateparklinglot.src.services;

import java.util.List;
import java.util.Optional;

import multigateparklinglot.src.enums.VehicleType;
import multigateparklinglot.src.models.parking.ParkingFloor;
import multigateparklinglot.src.models.parking.ParkingSpot;
import multigateparklinglot.src.models.vehicle.Vehicle;
import multigateparklinglot.src.repository.ParkingRepository;

public class ParkingService {
    private final ParkingRepository parkingRepository;

    public ParkingService(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    public void addParkingFloor(int floorNumber) {
        parkingRepository.addParkingFloor(new ParkingFloor(floorNumber));
    }

    public void addNewParkingSpotToFloor(int floorNumber, VehicleType vehicleType) {
        ParkingSpot parkingSpot = new ParkingSpot(floorNumber, vehicleType);
        parkingRepository.addSpotToParkingFloor(floorNumber, parkingSpot);
    }

    public Optional<ParkingSpot> findAvailableSpotForVehicle(Vehicle vehicle) {
        List<ParkingSpot> availableSpots = parkingRepository.getAllParkingSpots();
        for (ParkingSpot spot : availableSpots) {
            if (spot.getAllowedVehicleType() == vehicle.getVehicleType()) {
                return Optional.of(spot);
            }
        }
        return Optional.empty();
    }

    public void parkVehicle(Vehicle vehicle, ParkingSpot parkingSpot) {
        parkingSpot.parkVehicle(vehicle);
    }

    public void vacateParkingSpot(ParkingSpot parkingSpot) {
        parkingSpot.vacate();
    }

}
