package parkinglot.src.vehicle;

import parkinglot.src.fee.ParkingFeeStrategy;

public class Others extends Vehicle {
    public Others(String licensePlate, ParkingFeeStrategy parkingFeeStrategy) {
        super(licensePlate, VehicleType.OTHERS, parkingFeeStrategy);
    }

}
