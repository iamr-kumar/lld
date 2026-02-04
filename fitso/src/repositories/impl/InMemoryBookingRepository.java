package fitso.src.repositories.impl;

import fitso.src.models.Booking;
import fitso.src.repositories.BookingRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of BookingRepository using ConcurrentHashMap for
 * thread safety.
 */
public class InMemoryBookingRepository implements BookingRepository {

    private final ConcurrentHashMap<String, Booking> bookings = new ConcurrentHashMap<>();

    @Override
    public Booking save(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }
        bookings.put(booking.getBookingId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(String bookingId) {
        if (bookingId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(bookings.get(bookingId));
    }

    @Override
    public List<Booking> findByUserId(String userId) {
        if (userId == null) {
            return List.of();
        }

        return bookings.values().stream()
                .filter(booking -> userId.equals(booking.getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findBySlotId(String slotId) {
        if (slotId == null) {
            return List.of();
        }

        return bookings.values().stream()
                .filter(booking -> slotId.equals(booking.getSlotId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Booking> findActiveBookingByUserAndSlot(String userId, String slotId) {
        if (userId == null || slotId == null) {
            return Optional.empty();
        }

        return bookings.values().stream()
                .filter(booking -> userId.equals(booking.getUserId()) &&
                        slotId.equals(booking.getSlotId()) &&
                        booking.isActive())
                .findFirst();
    }

    @Override
    public List<Booking> findActiveBookingsByUserId(String userId) {
        if (userId == null) {
            return List.of();
        }

        return bookings.values().stream()
                .filter(booking -> userId.equals(booking.getUserId()) && booking.isActive())
                .collect(Collectors.toList());
    }

    @Override
    public Booking update(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }

        if (!bookings.containsKey(booking.getBookingId())) {
            throw new IllegalArgumentException("Booking not found: " + booking.getBookingId());
        }

        bookings.put(booking.getBookingId(), booking);
        return booking;
    }

    @Override
    public boolean deleteById(String bookingId) {
        return bookingId != null && bookings.remove(bookingId) != null;
    }
}
