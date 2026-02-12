package multigateparklinglot.src.models.gate;

import multigateparklinglot.src.engine.ParkingEngine;
import multigateparklinglot.src.enums.GateType;
import multigateparklinglot.src.enums.PaymentType;

public class ExitGate extends Gate {
    public ExitGate(ParkingEngine parkingEngine) {
        super(GateType.EXIT, parkingEngine);
    }

    public boolean processExit(String ticketId, String vehicleNumber, PaymentType paymentType) {
        return parkingEngine.unparkVehicle(ticketId, paymentType);
    }
}
