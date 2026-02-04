package fitso.src.repositories;

import fitso.src.models.Booking;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Booking data access operations.
 */
public interface BookingRepository {

    /**
     * Saves a booking to the repository.
     * 
     * @param booking the booking to save
     * @return the saved booking
     */
    Booking save(Booking booking);

    /**
     * Finds a booking by its unique ID.
     * 
     * @param bookingId the booking ID to search for
     * @return Optional containing the booking if found, empty otherwise
     */
    Optional<Booking> findById(String bookingId);

    /**
     * Finds all bookings for a specific user.
     * 
     * @param userId the user ID to search for
     * @return list of bookings for the user
     */
    List<Booking> findByUserId(String userId);

    /**
     * Finds all bookings for a specific slot.
     * 
     * @param slotId the slot ID to search for
     * @return list of bookings for the slot
     */
    List<Booking> findBySlotId(String slotId);

    /**
     * Finds active bookings for a specific user and slot.
     * 
     * @param userId the user ID
     * @param slotId the slot ID
     * @return Optional containing the active booking if found, empty otherwise
     */
    Optional<Booking> findActiveBookingByUserAndSlot(String userId, String slotId);

    /**
     * Finds all active bookings for a user.
     * 
     * @param userId the user ID
     * @return list of active bookings for the user
     */
    List<Booking> findActiveBookingsByUserId(String userId);

    /**
     * Updates a booking in the repository.
     * 
     * @param booking the booking to update
     * @return the updated booking
     */
    Booking update(Booking booking);

    /**
     * Deletes a booking by its ID.
     * 
     * @param bookingId the booking ID to delete
     * @return true if booking was deleted, false if not found
     */
    boolean deleteById(String bookingId);
}
