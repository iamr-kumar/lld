package parkinglot.vehicle;

import parkinglot.fee.ParkingFeeStrategy;

public class Car extends Vehicle {
    public Car(String licensePlate, ParkingFeeStrategy parkingFeeStrategy) {
        super(licensePlate, VehicleType.CAR, parkingFeeStrategy);
    }
}
