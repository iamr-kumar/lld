package multigateparklinglot.src.models.vehicle;

import multigateparklinglot.src.enums.VehicleType;

public class Others extends Vehicle {
    public Others(String licensePlate) {
        super(licensePlate, VehicleType.OTHERS);
    }
}
