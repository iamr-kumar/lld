package parkinglot.src.vehicle;

import parkinglot.src.fee.ParkingFeeStrategy;

public abstract class Vehicle {
    private String licensePlate;
    private VehicleType type;
    private ParkingFeeStrategy parkingFeeStrategy;

    public Vehicle(String licensePlate, VehicleType type, ParkingFeeStrategy parkingFeeStrategy) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.parkingFeeStrategy = parkingFeeStrategy;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getType() {
        return type;
    }

    public ParkingFeeStrategy getParkingFeeStrategy() {
        return parkingFeeStrategy;
    }

    public double calculateParkingFee(int hours) {
        return parkingFeeStrategy.calculateFee(hours);
    }
}
