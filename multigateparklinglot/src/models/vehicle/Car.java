package multigateparklinglot.src.models.vehicle;

import multigateparklinglot.src.enums.VehicleType;

public class Car extends Vehicle {
    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR);
    }
}
