package fitso.src.services.impl;

import fitso.src.models.Booking;
import fitso.src.models.WorkoutSlot;
import fitso.src.repositories.BookingRepository;
import fitso.src.repositories.WorkoutSlotRepository;
import fitso.src.services.BookingService;
import fitso.src.services.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of BookingService with business logic for booking operations.
 * Implements thread-safe booking with proper concurrency handling.
 */
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final WorkoutSlotRepository slotRepository;
    private final UserService userService;

    public BookingServiceImpl(BookingRepository bookingRepository,
            WorkoutSlotRepository slotRepository,
            UserService userService) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.userService = userService;
    }

    @Override
    public synchronized Booking bookSlot(String userId, String slotId) {
        // Validate input parameters
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (slotId == null || slotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Slot ID cannot be null or empty");
        }

        // Validate user exists
        if (!userService.validateUser(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        // Find the workout slot
        Optional<WorkoutSlot> slotOpt = slotRepository.findById(slotId);
        if (slotOpt.isEmpty()) {
            throw new IllegalArgumentException("Workout slot not found: " + slotId);
        }

        WorkoutSlot slot = slotOpt.get();

        // Check if user already has an active booking for this slot
        if (hasActiveBooking(userId, slotId)) {
            throw new IllegalArgumentException("User already has an active booking for this slot");
        }

        // Attempt to book a seat (thread-safe operation)
        if (!slot.bookSeat()) {
            throw new IllegalArgumentException("No available seats in this workout slot");
        }

        try {
            // Create booking
            String bookingId = generateBookingId();
            Booking booking = new Booking(bookingId, userId, slotId, LocalDateTime.now());

            // Save booking
            return bookingRepository.save(booking);
        } catch (Exception e) {
            // If booking creation fails, release the seat
            slot.cancelSeat();
            throw new RuntimeException("Failed to create booking: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized Booking cancelBooking(String userId, String slotId) {
        // Validate input parameters
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (slotId == null || slotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Slot ID cannot be null or empty");
        }

        // Validate user exists
        if (!userService.validateUser(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        // Find the active booking
        Optional<Booking> bookingOpt = bookingRepository.findActiveBookingByUserAndSlot(userId, slotId);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("No active booking found for user " + userId + " and slot " + slotId);
        }

        Booking booking = bookingOpt.get();

        // Find the workout slot
        Optional<WorkoutSlot> slotOpt = slotRepository.findById(slotId);
        if (slotOpt.isEmpty()) {
            throw new IllegalArgumentException("Workout slot not found: " + slotId);
        }

        WorkoutSlot slot = slotOpt.get();

        // Cancel the booking
        Booking cancelledBooking = booking.cancel();

        // Free up the seat (thread-safe operation)
        slot.cancelSeat();

        // Update booking in repository
        return bookingRepository.update(cancelledBooking);
    }

    @Override
    public List<Booking> getUserBookings(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public List<Booking> getUserActiveBookings(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        return bookingRepository.findActiveBookingsByUserId(userId);
    }

    @Override
    public List<Booking> getSlotBookings(String slotId) {
        if (slotId == null || slotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Slot ID cannot be null or empty");
        }
        return bookingRepository.findBySlotId(slotId);
    }

    @Override
    public Optional<Booking> findBookingById(String bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    public boolean hasActiveBooking(String userId, String slotId) {
        if (userId == null || slotId == null) {
            return false;
        }
        return bookingRepository.findActiveBookingByUserAndSlot(userId, slotId).isPresent();
    }

    /**
     * Generates a unique booking ID.
     * 
     * @return unique booking ID
     */
    private String generateBookingId() {
        return "BOOK_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
