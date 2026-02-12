package multigateparklinglot.src.models.vehicle;

import multigateparklinglot.src.enums.VehicleType;

public class Bike extends Vehicle {
    public Bike(String licensePlate) {
        super(licensePlate, VehicleType.BIKE);
    }
}
