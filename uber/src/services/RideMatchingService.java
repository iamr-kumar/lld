package uber.src.services;

import java.util.List;
import java.util.Optional;

import uber.src.enums.DriverResponse;
import uber.src.models.Driver;
import uber.src.models.Ride;
import uber.src.repositories.IDriverRepository;
import uber.src.strategies.IDriverMatchingStrategy;

public class RideMatchingService implements IRideMatchingService {
    private final IDriverRepository driverRepository;
    private final IDriverMatchingStrategy driverMatchingStrategy;
    private final IDriverNotificationService driverNotificationService;

    private static final int MAX_DRIVER_ATTEMPTS = 5;

    public RideMatchingService(IDriverRepository driverRepository, IDriverMatchingStrategy driverMatchingStrategy,
            IDriverNotificationService driverNotificationService) {
        this.driverRepository = driverRepository;
        this.driverMatchingStrategy = driverMatchingStrategy;
        this.driverNotificationService = driverNotificationService;
    }

    @Override
    public Optional<Driver> findAndAssignDriver(Ride ride) {
        System.out.println("Finding driver for ride " + ride.getId());
        int attempts = 0;

        while (attempts < MAX_DRIVER_ATTEMPTS) {
            attempts++;
            System.out.println("Attempt " + attempts + " to find driver for ride " + ride.getId());
            List<Driver> availableDrivers = driverRepository.getAvailableDrivers();
            if (availableDrivers.isEmpty()) {
                System.out.println("No available drivers at the moment for ride " + ride.getId());
                continue;
            }
            Optional<Driver> driverOpt = driverMatchingStrategy.findDriver(ride, availableDrivers,
                    ride.getExcludeDriverIds());
            if (driverOpt.isEmpty()) {
                System.out.println("No suitable driver found in attempt " + attempts + " for ride " + ride.getId());
                continue;
            }
            Driver driver = driverOpt.get();
            boolean locked = driver.tryLockForRide();
            if (!locked) {
                System.out.println("Driver " + driver.getId() + " is no longer available for ride " + ride.getId());
                continue;
            }
            System.out.println("Driver " + driver.getId() + " locked for ride " + ride.getId());
            DriverResponse response = driverNotificationService.sendRequestToDriverAndWaitForResponse(driver, ride);
            switch (response) {
                case ACCEPT:
                    driver.assignToRide();
                    System.out.println("Driver " + driver.getId() + " accepted ride " + ride.getId());
                    return Optional.of(driver);
                case REJECT:
                default:
                    driver.releaseLock();
                    System.out.println("Driver " + driver.getId() + " rejected ride " + ride.getId());
                    ride.excludeDriver(driver.getId());
                    break;
            }
        }
        System.out.println("Failed to find a driver for ride " + ride.getId() + " after " + attempts + " attempts");
        return Optional.empty();
    }
}
