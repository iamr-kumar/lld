package multigateparklinglot.src.models.gate;

import java.util.Optional;

import multigateparklinglot.src.engine.ParkingEngine;
import multigateparklinglot.src.enums.FeeType;
import multigateparklinglot.src.enums.GateType;
import multigateparklinglot.src.enums.VehicleType;

public class EntryGate extends Gate {

    public EntryGate(ParkingEngine parkingEngine) {
        super(GateType.ENTRY, parkingEngine);
    }

    public Optional<String> processEntry(String vehicleNumber, VehicleType vehicleType, FeeType feeType) {
        return parkingEngine.parkVehicle(vehicleNumber, vehicleType, feeType);
    }
}
