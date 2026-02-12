package multigateparklinglot.src.models.parking;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import multigateparklinglot.src.enums.ParkingLotState;
import multigateparklinglot.src.enums.VehicleType;
import multigateparklinglot.src.models.vehicle.Vehicle;

public class ParkingSpot {
    private final int floor;
    private final String lotNumber;
    private final VehicleType allowedVehicleType;
    private Vehicle parkedVehicle;
    private AtomicReference<ParkingLotState> state;

    public ParkingSpot(int floor, VehicleType allowedVehicleType) {
        this.floor = floor;
        this.lotNumber = UUID.randomUUID().toString();
        this.allowedVehicleType = allowedVehicleType;
        this.state = new AtomicReference<>(ParkingLotState.AVAILABLE);
    }

    public int getFloor() {
        return floor;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public VehicleType getAllowedVehicleType() {
        return allowedVehicleType;
    }

    public boolean isAvailable() {
        return state.get() == ParkingLotState.AVAILABLE;
    }

    public void parkVehicle(Vehicle vehicle) {
        if (state.compareAndSet(ParkingLotState.AVAILABLE, ParkingLotState.OCCUPIED)) {
            this.parkedVehicle = vehicle;
        } else {
            throw new IllegalStateException("Parking lot is already occupied");
        }
    }

    public void vacate() {
        if (state.compareAndSet(ParkingLotState.OCCUPIED, ParkingLotState.AVAILABLE)) {
            this.parkedVehicle = null;
        } else {
            throw new IllegalStateException("Parking lot is already available");
        }
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }
}
