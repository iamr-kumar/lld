package models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a booking made by a user for a workout slot.
 * Immutable class representing the booking transaction.
 */
public class Booking {
    private final String bookingId;
    private final String userId;
    private final String slotId;
    private final LocalDateTime bookingTime;
    private final BookingStatus status;

    public enum BookingStatus {
        CONFIRMED,
        CANCELLED
    }

    public Booking(String bookingId, String userId, String slotId, LocalDateTime bookingTime) {
        this(bookingId, userId, slotId, bookingTime, BookingStatus.CONFIRMED);
    }

    public Booking(String bookingId, String userId, String slotId,
            LocalDateTime bookingTime, BookingStatus status) {
        this.bookingId = Objects.requireNonNull(bookingId, "Booking ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.slotId = Objects.requireNonNull(slotId, "Slot ID cannot be null");
        this.bookingTime = Objects.requireNonNull(bookingTime, "Booking time cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSlotId() {
        return slotId;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public boolean isActive() {
        return status == BookingStatus.CONFIRMED;
    }

    /**
     * Creates a new booking instance with cancelled status.
     * Following immutability pattern.
     */
    public Booking cancel() {
        return new Booking(bookingId, userId, slotId, bookingTime, BookingStatus.CANCELLED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Booking booking = (Booking) o;
        return Objects.equals(bookingId, booking.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", userId='" + userId + '\'' +
                ", slotId='" + slotId + '\'' +
                ", bookingTime=" + bookingTime +
                ", status=" + status +
                '}';
    }
}
