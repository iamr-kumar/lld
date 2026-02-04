package parkinglot.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import parkinglot.src.fee.ParkingFeeStrategy;
import parkinglot.src.payment.Payment;
import parkinglot.src.payment.PaymentMode;
import parkinglot.src.vehicle.Vehicle;

public class ParkingLot {
    private static ParkingLot instance;
    private List<ParkingFloor> floors;
    private final Map<String, Ticket> tickets;

    private ParkingLot() {
        this.floors = new ArrayList<>();
        this.tickets = new HashMap<>();
    }

    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }

    public void addFloor(ParkingFloor floor) {
        floors.add(floor);
    }

    public synchronized Ticket parkVehicle(Vehicle vehicle) throws Exception {
        for (ParkingFloor floor : floors) {
            Optional<ParkingSpot> availableSpot = floor.findAvailableSpot(vehicle.getType());
            if (availableSpot.isEmpty()) {
                continue;
            }
            ParkingSpot parkingSpot = availableSpot.get();
            if (parkingSpot.parkVehicle(vehicle)) {
                String ticketId = UUID.randomUUID().toString();
                Ticket ticket = new Ticket(ticketId, vehicle, parkingSpot);
                tickets.put(ticketId, ticket);
                return ticket;
            }
        }
        throw new Exception("No available parking spot for vehicle: " + vehicle);
    }

    public Ticket getTicket(String ticketId) {
        return tickets.get(ticketId);
    }

    public void removeTicket(String ticketId) {
        tickets.remove(ticketId);
    }

    public synchronized boolean unparkVehicle(String ticketId, PaymentMode paymentMode,
            Map<String, String> paymentConfig) throws Exception {
        try {
            Ticket ticket = this.getTicket(ticketId);
            if (ticket == null) {
                throw new Exception("Ticket not found: " + ticketId);
            }
            int durationInHours = ticket.getDurationInHours();
            Vehicle vehicle = ticket.getVehicle();
            ParkingFeeStrategy feeStrategy = vehicle.getParkingFeeStrategy();
            double amount = feeStrategy.calculateFee(durationInHours);
            ParkingSpot parkingSpot = ticket.getParkingSpot();
            if (!parkingSpot.removeVehicle()) {
                throw new Exception("Failed to remove vehicle from parking spot: " + parkingSpot.getId());
            }
            Payment payment = new Payment(amount, ticket, paymentMode, paymentConfig);
            payment.setPaymentStrategy();
            if (!payment.processPayment()) {
                throw new Exception("Payment failed for ticket: " + ticketId);
            }
            this.removeTicket(ticketId);
            System.out.println(
                    "Vehicle with license plate " + vehicle.getLicensePlate() + " has been successfully unparked.");
            return true;
        } catch (Exception e) {
            System.err.println("Error during unparking vehicle: " + e.getMessage());
            return false;
        }

    }

}
