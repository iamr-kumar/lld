package parkinglot.vehicle;

import parkinglot.fee.ParkingFeeStrategy;

public class Bike extends Vehicle {
    public Bike(String licensePlate, ParkingFeeStrategy parkingFeeStrategy) {
        super(licensePlate, VehicleType.BIKE, parkingFeeStrategy);
    }
}
