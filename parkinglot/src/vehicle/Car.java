package parkinglot.src.vehicle;

import parkinglot.src.fee.ParkingFeeStrategy;

public class Car extends Vehicle {
    public Car(String licensePlate, ParkingFeeStrategy parkingFeeStrategy) {
        super(licensePlate, VehicleType.CAR, parkingFeeStrategy);
    }
}
