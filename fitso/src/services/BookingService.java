package services;

import models.Booking;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Booking-related business operations.
 */
public interface BookingService {

    /**
     * Books a workout slot for a user.
     * 
     * @param userId the user ID making the booking
     * @param slotId the slot ID to book
     * @return the created booking
     * @throws IllegalArgumentException if user doesn't exist, slot doesn't exist,
     *                                  no seats available, or user already has a
     *                                  booking for this slot
     */
    Booking bookSlot(String userId, String slotId);

    /**
     * Cancels a booking for a user.
     * 
     * @param userId the user ID cancelling the booking
     * @param slotId the slot ID to cancel
     * @return the cancelled booking
     * @throws IllegalArgumentException if user doesn't exist, slot doesn't exist,
     *                                  or no active booking found
     */
    Booking cancelBooking(String userId, String slotId);

    /**
     * Gets all bookings for a specific user.
     * 
     * @param userId the user ID
     * @return list of bookings for the user
     */
    List<Booking> getUserBookings(String userId);

    /**
     * Gets all active bookings for a specific user.
     * 
     * @param userId the user ID
     * @return list of active bookings for the user
     */
    List<Booking> getUserActiveBookings(String userId);

    /**
     * Gets all bookings for a specific slot.
     * 
     * @param slotId the slot ID
     * @return list of bookings for the slot
     */
    List<Booking> getSlotBookings(String slotId);

    /**
     * Finds a booking by its ID.
     * 
     * @param bookingId the booking ID
     * @return Optional containing the booking if found, empty otherwise
     */
    Optional<Booking> findBookingById(String bookingId);

    /**
     * Checks if a user has an active booking for a specific slot.
     * 
     * @param userId the user ID
     * @param slotId the slot ID
     * @return true if user has an active booking for the slot, false otherwise
     */
    boolean hasActiveBooking(String userId, String slotId);
}
