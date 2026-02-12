package multigateparklinglot.src.models.ticket;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import multigateparklinglot.src.enums.FeeType;
import multigateparklinglot.src.factory.FeeFactory;
import multigateparklinglot.src.models.parking.ParkingSpot;
import multigateparklinglot.src.models.vehicle.Vehicle;
import multigateparklinglot.src.strategy.fee.FeeCalculationStrategy;

public class Ticket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final LocalDateTime entryTime;
    private final ParkingSpot assignedParkingSpot;
    private final FeeCalculationStrategy feeCalculationStrategy;
    private LocalDateTime exitTime;

    public Ticket(Vehicle vehicle, ParkingSpot assignedParkingSpot, FeeType feeType) {
        this.ticketId = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.assignedParkingSpot = assignedParkingSpot;
        this.entryTime = LocalDateTime.now();
        this.feeCalculationStrategy = FeeFactory.getFeeCalculationStrategy(feeType);
    }

    public String getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setExitTime() {
        this.exitTime = LocalDateTime.now();
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public FeeCalculationStrategy getFeeCalculationStrategy() {
        return feeCalculationStrategy;
    }

    public ParkingSpot getAssignedParkingSpot() {
        return assignedParkingSpot;
    }

    public int getDurationInMinutes() {
        if (exitTime == null) {
            throw new IllegalStateException("Exit time is not yet set");
        }
        return (int) Duration.between(entryTime, exitTime).toMinutes();
    }

}
