package fitso.src;

import fitso.src.config.ApplicationConfig;
import fitso.src.models.Booking;
import fitso.src.models.FitnessCenter;
import fitso.src.models.User;
import fitso.src.models.WorkoutSlot;
import fitso.src.models.WorkoutType;
import fitso.src.services.BookingService;
import fitso.src.services.FitnessCenterService;
import fitso.src.services.UserService;
import fitso.src.services.WorkoutSlotService;
import java.util.List;
import java.util.Set;

/**
 * Main application class demonstrating the Fitso App functionality.
 * This class showcases all the key features and edge cases of the system.
 */
public class FitsoApplication {

    private final UserService userService;
    private final FitnessCenterService centerService;
    private final WorkoutSlotService slotService;
    private final BookingService bookingService;

    public FitsoApplication() {
        ApplicationConfig config = ApplicationConfig.getInstance();
        this.userService = config.getUserService();
        this.centerService = config.getCenterService();
        this.slotService = config.getSlotService();
        this.bookingService = config.getBookingService();
    }

    public static void main(String[] args) {
        System.out.println("=== Welcome to Fitso App ===");
        System.out.println("Fitness Center Management & Booking System");
        System.out.println("==========================================\n");

        FitsoApplication app = new FitsoApplication();
        app.runDemo();
    }

    /**
     * Runs a comprehensive demo of the Fitso App functionality.
     */
    public void runDemo() {
        try {
            // 1. Setup initial data
            setupInitialData();

            // 2. Demonstrate user registration
            demonstrateUserRegistration();

            // 3. Demonstrate center onboarding
            demonstrateCenterOnboarding();

            // 4. Demonstrate slot creation
            demonstrateSlotCreation();

            // 5. Demonstrate slot viewing and filtering
            demonstrateSlotViewing();

            // 6. Demonstrate booking functionality
            demonstrateBookingFunctionality();

            // 7. Demonstrate cancellation functionality
            demonstrateCancellationFunctionality();

            // 8. Demonstrate concurrency handling
            demonstrateConcurrencyHandling();

        } catch (Exception e) {
            System.err.println("Error occurred during demo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupInitialData() {
        System.out.println("1. Setting up initial data...");

        // Register some users
        userService.registerUser("user1", "John Doe", "john@email.com");
        userService.registerUser("user2", "Jane Smith", "jane@email.com");
        userService.registerUser("user3", "Bob Wilson", "bob@email.com");

        System.out.println("✓ Users registered successfully\n");
    }

    private void demonstrateUserRegistration() {
        System.out.println("2. Demonstrating User Registration...");

        try {
            User newUser = userService.registerUser("user4", "Alice Brown", "alice@email.com");
            System.out.println("✓ New user registered: " + newUser.getName());

            // Try to register with duplicate email (should fail)
            try {
                userService.registerUser("user5", "Charlie Davis", "alice@email.com");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Duplicate email validation working: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("✗ User registration failed: " + e.getMessage());
        }

        System.out.println();
    }

    private void demonstrateCenterOnboarding() {
        System.out.println("3. Demonstrating Fitness Center Onboarding...");

        try {
            // Onboard fitness centers
            FitnessCenter center1 = centerService.onboardCenter(
                    "center1", "FitZone Downtown", 6, 22, 20,
                    Set.of(WorkoutType.WEIGHTS, WorkoutType.CARDIO, WorkoutType.YOGA));
            System.out.println("✓ Center onboarded: " + center1.getName());

            FitnessCenter center2 = centerService.onboardCenter(
                    "center2", "AquaFit Sports", 7, 21, 15,
                    Set.of(WorkoutType.SWIMMING, WorkoutType.YOGA, WorkoutType.PILATES));
            System.out.println("✓ Center onboarded: " + center2.getName());

            FitnessCenter center3 = centerService.onboardCenter(
                    "center3", "PowerGym Elite", 5, 23, 25,
                    Set.of(WorkoutType.WEIGHTS, WorkoutType.CROSSFIT, WorkoutType.CARDIO));
            System.out.println("✓ Center onboarded: " + center3.getName());

        } catch (Exception e) {
            System.err.println("✗ Center onboarding failed: " + e.getMessage());
        }

        System.out.println();
    }

    private void demonstrateSlotCreation() {
        System.out.println("4. Demonstrating Workout Slot Creation...");

        try {
            // Create slots for center1
            slotService.createWorkoutSlot("slot1", "center1", WorkoutType.WEIGHTS, 8, 2);
            slotService.createWorkoutSlot("slot2", "center1", WorkoutType.CARDIO, 10, 1);
            slotService.createWorkoutSlot("slot3", "center1", WorkoutType.YOGA, 18, 1);

            // Create slots for center2
            slotService.createWorkoutSlot("slot4", "center2", WorkoutType.SWIMMING, 9, 1);
            slotService.createWorkoutSlot("slot5", "center2", WorkoutType.YOGA, 17, 1);

            // Create slots for center3
            slotService.createWorkoutSlot("slot6", "center3", WorkoutType.WEIGHTS, 7, 2);
            slotService.createWorkoutSlot("slot7", "center3", WorkoutType.CROSSFIT, 19, 1);

            System.out.println("✓ All workout slots created successfully");

            // Try to create slot outside operating hours (should fail)
            try {
                slotService.createWorkoutSlot("slot_invalid", "center1", WorkoutType.WEIGHTS, 23, 2);
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Operating hours validation working: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("✗ Slot creation failed: " + e.getMessage());
        }

        System.out.println();
    }

    private void demonstrateSlotViewing() {
        System.out.println("5. Demonstrating Slot Viewing and Filtering...");

        try {
            // View all available slots
            List<WorkoutSlot> availableSlots = slotService.getAvailableSlots();
            System.out.println("All available slots (" + availableSlots.size() + "):");
            availableSlots
                    .forEach(slot -> System.out.println("  - " + slot.getWorkoutType() + " at " + slot.getStartTime() +
                            ":00 (" + slot.getAvailableSeats() + " seats)"));

            // Filter by workout type (sorted by time)
            List<WorkoutSlot> yogaSlots = slotService.getSlotsByWorkoutType(WorkoutType.YOGA);
            System.out.println("\nYoga slots (sorted by time):");
            yogaSlots.forEach(slot -> System.out
                    .println("  - " + slot.getStartTime() + ":00 (" + slot.getAvailableSeats() + " seats)"));

            // Filter by workout type and center (sorted by available seats)
            List<WorkoutSlot> center1WeightsSlots = slotService.getSlotsByWorkoutTypeAndCenter(WorkoutType.WEIGHTS,
                    "center1");
            System.out.println("\nWeights slots at FitZone Downtown (sorted by available seats):");
            center1WeightsSlots.forEach(slot -> System.out
                    .println("  - " + slot.getStartTime() + ":00 (" + slot.getAvailableSeats() + " seats)"));

        } catch (Exception e) {
            System.err.println("✗ Slot viewing failed: " + e.getMessage());
        }

        System.out.println();
    }

    private void demonstrateBookingFunctionality() {
        System.out.println("6. Demonstrating Booking Functionality...");

        try {
            // Make some bookings
            Booking booking1 = bookingService.bookSlot("user1", "slot1");
            System.out.println("✓ Booking created: " + booking1.getBookingId());

            Booking booking2 = bookingService.bookSlot("user2", "slot1");
            System.out.println("✓ Booking created: " + booking2.getBookingId());

            Booking booking3 = bookingService.bookSlot("user3", "slot4");
            System.out.println("✓ Booking created: " + booking3.getBookingId());

            // Try to book the same slot again (should fail)
            try {
                bookingService.bookSlot("user1", "slot1");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Duplicate booking validation working: " + e.getMessage());
            }

            // Check updated slot availability
            WorkoutSlot updatedSlot = slotService.findSlotById("slot1").orElse(null);
            if (updatedSlot != null) {
                System.out.println("✓ Slot1 now has " + updatedSlot.getAvailableSeats() + " available seats");
            }

        } catch (Exception e) {
            System.err.println("✗ Booking failed: " + e.getMessage());
        }

        System.out.println();
    }

    private void demonstrateCancellationFunctionality() {
        System.out.println("7. Demonstrating Cancellation Functionality...");

        try {
            // Cancel a booking
            Booking cancelledBooking = bookingService.cancelBooking("user2", "slot1");
            System.out.println("✓ Booking cancelled: " + cancelledBooking.getBookingId());

            // Check updated slot availability
            WorkoutSlot updatedSlot = slotService.findSlotById("slot1").orElse(null);
            if (updatedSlot != null) {
                System.out.println("✓ Slot1 now has " + updatedSlot.getAvailableSeats() + " available seats");
            }

            // Try to cancel the same booking again (should fail)
            try {
                bookingService.cancelBooking("user2", "slot1");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Double cancellation validation working: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("✗ Cancellation failed: " + e.getMessage());
        }

        System.out.println();
    }

    private void demonstrateConcurrencyHandling() {
        System.out.println("8. Demonstrating Concurrency Handling...");

        try {
            // Create a slot with limited capacity for testing
            slotService.createWorkoutSlot("concurrency_test", "center1", WorkoutType.CARDIO, 15, 1);

            // Simulate concurrent booking attempts
            Thread[] threads = new Thread[5];
            String[] userIds = { "user1", "user2", "user3", "user4", "user_extra" };

            for (int i = 0; i < 5; i++) {
                final String userId = userIds[i];
                threads[i] = new Thread(() -> {
                    try {
                        Booking booking = bookingService.bookSlot(userId, "concurrency_test");
                        System.out.println(
                                "✓ Concurrent booking successful for " + userId + ": " + booking.getBookingId());
                    } catch (Exception e) {
                        System.out.println("✗ Concurrent booking failed for " + userId + ": " + e.getMessage());
                    }
                });
            }

            // Start all threads
            for (Thread thread : threads) {
                thread.start();
            }

            // Wait for all threads to complete
            for (Thread thread : threads) {
                thread.join();
            }

            // Check final slot state
            WorkoutSlot testSlot = slotService.findSlotById("concurrency_test").orElse(null);
            if (testSlot != null) {
                System.out.println(
                        "✓ Final slot capacity: " + testSlot.getAvailableSeats() + "/" + testSlot.getTotalCapacity());
            }

        } catch (Exception e) {
            System.err.println("✗ Concurrency test failed: " + e.getMessage());
        }

        System.out.println("\n=== Demo completed successfully! ===");
    }
}
