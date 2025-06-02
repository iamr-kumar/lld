package parkinglot.src.vehicle;

import parkinglot.src.fee.ParkingFeeStrategy;

public class Bike extends Vehicle {
    public Bike(String licensePlate, ParkingFeeStrategy parkingFeeStrategy) {
        super(licensePlate, VehicleType.BIKE, parkingFeeStrategy);
    }
}
