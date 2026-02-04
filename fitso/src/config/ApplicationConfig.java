package fitso.src.config;

import fitso.src.repositories.BookingRepository;
import fitso.src.repositories.FitnessCenterRepository;
import fitso.src.repositories.UserRepository;
import fitso.src.repositories.WorkoutSlotRepository;
import fitso.src.repositories.impl.InMemoryBookingRepository;
import fitso.src.repositories.impl.InMemoryFitnessCenterRepository;
import fitso.src.repositories.impl.InMemoryUserRepository;
import fitso.src.repositories.impl.InMemoryWorkoutSlotRepository;
import fitso.src.services.BookingService;
import fitso.src.services.FitnessCenterService;
import fitso.src.services.UserService;
import fitso.src.services.WorkoutSlotService;
import fitso.src.services.impl.BookingServiceImpl;
import fitso.src.services.impl.FitnessCenterServiceImpl;
import fitso.src.services.impl.UserServiceImpl;
import fitso.src.services.impl.WorkoutSlotServiceImpl;

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
