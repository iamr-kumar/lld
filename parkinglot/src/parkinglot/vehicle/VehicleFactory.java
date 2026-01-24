package parkinglot.vehicle;

import parkinglot.fee.ParkingFeeStrategy;

public class VehicleFactory {
    public static Vehicle createVehicle(String licensePlate, VehicleType vehicleType,
            ParkingFeeStrategy parkingFeeStrategy) {

        switch (vehicleType) {
            case CAR:
                return new Car(licensePlate, parkingFeeStrategy);
            case BIKE:
                return new Bike(licensePlate, parkingFeeStrategy);
            default:
                return new Others(licensePlate, parkingFeeStrategy);
        }
    }
}
