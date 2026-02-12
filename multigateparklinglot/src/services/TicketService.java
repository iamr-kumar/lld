package multigateparklinglot.src.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import multigateparklinglot.src.enums.FeeType;
import multigateparklinglot.src.models.parking.ParkingSpot;
import multigateparklinglot.src.models.ticket.Ticket;
import multigateparklinglot.src.models.vehicle.Vehicle;

public class TicketService {
    private final Map<String, Ticket> activeTickets;

    public TicketService() {
        this.activeTickets = new ConcurrentHashMap<>();
    }

    public Ticket createTicket(Vehicle vehicle, ParkingSpot parkingLot, FeeType feeType) {
        Ticket ticket = new Ticket(vehicle, parkingLot, feeType);
        activeTickets.put(ticket.getTicketId(), ticket);
        return ticket;
    }

    public Ticket getTicket(String ticketId) {
        return activeTickets.get(ticketId);
    }

    public void removeTicket(String ticketId) {
        activeTickets.remove(ticketId);
    }
}
