package parkinglot.vehicle;

import parkinglot.fee.ParkingFeeStrategy;

public class Others extends Vehicle {
    public Others(String licensePlate, ParkingFeeStrategy parkingFeeStrategy) {
        super(licensePlate, VehicleType.OTHERS, parkingFeeStrategy);
    }

}
