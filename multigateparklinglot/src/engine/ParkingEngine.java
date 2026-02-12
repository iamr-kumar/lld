package multigateparklinglot.src.engine;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import multigateparklinglot.src.enums.FeeType;
import multigateparklinglot.src.enums.PaymentType;
import multigateparklinglot.src.enums.VehicleType;
import multigateparklinglot.src.factory.VehicleFactory;
import multigateparklinglot.src.models.parking.ParkingSpot;
import multigateparklinglot.src.models.ticket.Ticket;
import multigateparklinglot.src.models.vehicle.Vehicle;
import multigateparklinglot.src.services.ParkingService;
import multigateparklinglot.src.services.PaymentService;
import multigateparklinglot.src.services.TicketService;

public class ParkingEngine {
    private final ParkingService parkingService;
    private final TicketService ticketService;
    private final PaymentService paymentService;
    private final Map<String, ReentrantLock> parkingLotLocks;
    private static final int MAX_RETRY_COUNT = 3;

    public ParkingEngine(ParkingService parkingService, TicketService ticketService, PaymentService paymentService) {
        this.parkingService = parkingService;
        this.ticketService = ticketService;
        this.paymentService = paymentService;
        this.parkingLotLocks = new ConcurrentHashMap<>();
    }

    public Optional<String> parkVehicle(String vehicleNumber, VehicleType vehicleType, FeeType feeType) {
        Vehicle vehicle = VehicleFactory.createVehicle(vehicleNumber, vehicleType);
        int retryCount = 0;
        while (retryCount < MAX_RETRY_COUNT) {
            Optional<ParkingSpot> availableLot = parkingService.findAvailableSpotForVehicle(vehicle);
            if (availableLot.isEmpty()) {
                System.out.println("No available parking lot for vehicle type: " + vehicleType);
                return Optional.empty();
            }
            ParkingSpot parkingSpot = availableLot.get();
            ReentrantLock lock = parkingLotLocks.computeIfAbsent(String.valueOf(parkingSpot.getLotNumber()),
                    k -> new ReentrantLock());
            lock.lock();
            try {
                if (parkingSpot.isAvailable()) {
                    parkingService.parkVehicle(vehicle, parkingSpot);
                    Ticket ticket = ticketService.createTicket(vehicle, parkingSpot, feeType);
                    return Optional.of(ticket.getTicketId());
                } else {
                    retryCount++;
                }
            } finally {
                lock.unlock();
            }

        }
        return Optional.empty();
    }

    public boolean unparkVehicle(String ticketId, PaymentType paymentType) {
        Ticket ticket = ticketService.getTicket(ticketId);
        if (ticket == null) {
            System.out.println("Invalid ticket ID: " + ticketId);
            return false;
        }
        ParkingSpot parkingSpot = ticket.getAssignedParkingSpot();
        ReentrantLock lock = parkingLotLocks.computeIfAbsent(parkingSpot.getLotNumber(),
                k -> new ReentrantLock());
        lock.lock();
        try {
            boolean paymentSuccess = paymentService.processPayment(ticket, paymentType);
            if (paymentSuccess) {
                parkingService.vacateParkingSpot(parkingSpot);
                ticketService.removeTicket(ticketId);
                return true;
            } else {
                System.out.println("Payment failed for ticket ID: " + ticketId);
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

}
