package multigateparklinglot.src.models.gate;

import java.util.UUID;

import multigateparklinglot.src.engine.ParkingEngine;
import multigateparklinglot.src.enums.GateType;

public abstract class Gate {
    protected final String gateId;
    protected final GateType gateType;
    protected final ParkingEngine parkingEngine;

    public Gate(GateType gateType, ParkingEngine parkingEngine) {
        this.gateId = UUID.randomUUID().toString();
        this.gateType = gateType;
        this.parkingEngine = parkingEngine;
    }

    public String getGateId() {
        return gateId;
    }

    public GateType getGateType() {
        return gateType;
    }
}
