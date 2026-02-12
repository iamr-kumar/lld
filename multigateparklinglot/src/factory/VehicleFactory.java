package multigateparklinglot.src.factory;

import multigateparklinglot.src.enums.VehicleType;
import multigateparklinglot.src.models.vehicle.Bike;
import multigateparklinglot.src.models.vehicle.Car;
import multigateparklinglot.src.models.vehicle.Others;
import multigateparklinglot.src.models.vehicle.Vehicle;

public class VehicleFactory {
    public static Vehicle createVehicle(String licensePlate, VehicleType type) {
        switch (type) {
            case CAR:
                return new Car(licensePlate);
            case BIKE:
                return new Bike(licensePlate);
            default:
                return new Others(licensePlate);
        }
    }
}
