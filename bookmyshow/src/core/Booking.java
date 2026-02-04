package bookmyshow.src.core;

import java.util.List;

import bookmyshow.src.types.BookingStatus;

public class Booking {
    private final String id;
    private final Show show;
    private final User user;
    private final List<Seat> seats;
    private BookingStatus status;

    public Booking(Show show, User user, List<Seat> seats) {
        this.id = java.util.UUID.randomUUID().toString(); // Using UUID for unique ID
        this.show = show;
        this.user = user;
        this.seats = seats;
        this.status = BookingStatus.CREATED; // Initial status
    }

    public String getId() {
        return id;
    }

    public Show getShow() {
        return show;
    }

    public User getUser() {
        return user;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public boolean isBookingConfirmed() {
        return status == BookingStatus.CONFIRMED;
    }

    public void confirmBooking() {
        if (status == BookingStatus.CREATED) {
            status = BookingStatus.CONFIRMED;
        } else {
            throw new IllegalStateException("Booking can only be confirmed if it is in CREATED state.");
        }
    }

    public void expireBooking() throws IllegalStateException {
        if (status == BookingStatus.CREATED) {
            status = BookingStatus.CANCELLED;
        } else {
            throw new IllegalStateException("Booking can only be cancelled if it is in CREATED state.");
        }
    }

}
