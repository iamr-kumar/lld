package config;

import repositories.BookingRepository;
import repositories.FitnessCenterRepository;
import repositories.UserRepository;
import repositories.WorkoutSlotRepository;
import repositories.impl.InMemoryBookingRepository;
import repositories.impl.InMemoryFitnessCenterRepository;
import repositories.impl.InMemoryUserRepository;
import repositories.impl.InMemoryWorkoutSlotRepository;
import services.BookingService;
import services.FitnessCenterService;
import services.UserService;
import services.WorkoutSlotService;
import services.impl.BookingServiceImpl;
import services.impl.FitnessCenterServiceImpl;
import services.impl.UserServiceImpl;
import services.impl.WorkoutSlotServiceImpl;

/**
 * Configuration class for dependency injection.
 * Implements Factory pattern for creating service instances.
 * 
 * Design Decision: Using manual dependency injection instead of a framework
 * to keep the solution simple and focused on the core business logic.
 */
public class ApplicationConfig {

    private static ApplicationConfig instance;

    // Repositories
    private final UserRepository userRepository;
    private final FitnessCenterRepository centerRepository;
    private final WorkoutSlotRepository slotRepository;
    private final BookingRepository bookingRepository;

    // Services
    private final UserService userService;
    private final FitnessCenterService centerService;
    private final WorkoutSlotService slotService;
    private final BookingService bookingService;

    private ApplicationConfig() {
        // Initialize repositories
        this.userRepository = new InMemoryUserRepository();
        this.centerRepository = new InMemoryFitnessCenterRepository();
        this.slotRepository = new InMemoryWorkoutSlotRepository();
        this.bookingRepository = new InMemoryBookingRepository();

        // Initialize services with dependency injection
        this.userService = new UserServiceImpl(userRepository);
        this.centerService = new FitnessCenterServiceImpl(centerRepository);
        this.slotService = new WorkoutSlotServiceImpl(slotRepository, centerRepository);
        this.bookingService = new BookingServiceImpl(bookingRepository, slotRepository, userService);
    }

    /**
     * Singleton instance getter.
     * Thread-safe lazy initialization.
     */
    public static synchronized ApplicationConfig getInstance() {
        if (instance == null) {
            instance = new ApplicationConfig();
        }
        return instance;
    }

    public UserService getUserService() {
        return userService;
    }

    public FitnessCenterService getCenterService() {
        return centerService;
    }

    public WorkoutSlotService getSlotService() {
        return slotService;
    }

    public BookingService getBookingService() {
        return bookingService;
    }
}
