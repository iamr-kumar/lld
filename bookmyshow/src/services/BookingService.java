package bookmyshow.src.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bookmyshow.src.core.Booking;
import bookmyshow.src.core.Seat;
import bookmyshow.src.core.Show;
import bookmyshow.src.core.User;
import bookmyshow.src.interaces.ISeatLockProvider;

public class BookingService {
    private final Map<String, Booking> bookings;
    private final ISeatLockProvider seatLockProvider;

    public BookingService(ISeatLockProvider seatLockProvider) {
        this.seatLockProvider = seatLockProvider;
        this.bookings = new HashMap<>();
    }

    public List<Booking> getAllBookings(Show show) {
        final List<Booking> response = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            if (booking.getShow().getId().equals(show.getId())) {
                response.add(booking);
            }
        }
        return response;
    }

    public Booking createBooking(Show show, User user, List<Seat> seats) throws Exception {
        if (this.isAnySeatBooked(show, seats)) {
            throw new Exception("Some seats are already booked.");
        }
        if (this.isAnySeatLocked(show, seats)) {
            throw new Exception("Some seats are already locked by another user.");
        }
        seatLockProvider.lockSeat(show, seats, user);
        Booking booking = new Booking(show, user, seats);
        bookings.put(booking.getId(), booking);
        return booking;
    }

    public List<Seat> getBookedSeats(Show show) {
        final List<Seat> bookedSeats = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            if (booking.isBookingConfirmed() && booking.getShow().getId().equals(show.getId())) {
                bookedSeats.addAll(booking.getSeats());
            }
        }
        return bookedSeats;
    }

    public Booking getBookingById(String bookingId) {
        return bookings.get(bookingId);
    }

    public boolean confirmBooking(Booking booking, User user) throws Exception {
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new Exception("User does not have permission to confirm this booking.");
        }
        for (Seat seat : booking.getSeats()) {
            if (!seatLockProvider.validateLock(seat, booking.getShow(), user)) {
                throw new Exception("Seat " + seat.getId() + " is not locked by the user.");
            }
        }
        booking.confirmBooking();
        return true;
    }

    public boolean expireBooking(Booking booking, User user) throws Exception {
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new Exception("User does not have permission to cancel this booking.");
        }
        seatLockProvider.unlockSeats(booking.getShow(), booking.getSeats(), user);
        booking.expireBooking();
        return true;
    }

    private boolean isAnySeatLocked(Show show, List<Seat> seats) throws Exception {
        final List<Seat> getLockedSeats = seatLockProvider.getLockedSeats(show);
        for (Seat seat : seats) {
            if (getLockedSeats.contains(seat)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnySeatBooked(Show show, List<Seat> seats) {
        final List<Seat> bookedSeats = this.getBookedSeats(show);
        for (Seat seat : seats) {
            if (bookedSeats.contains(seat)) {
                return true;
            }
        }
        return false;
    }
}
