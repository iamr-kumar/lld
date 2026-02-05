package uber.src.strategies;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import uber.src.models.Driver;
import uber.src.models.Ride;

public class NearestDriverStrategy implements IDriverMatchingStrategy {
    @Override
    public Optional<Driver> findDriver(Ride ride, List<Driver> availableDrivers, Set<String> excludeDriverIds) {
        Driver nearestDriver = null;
        double minDistance = Double.MAX_VALUE;

        for (Driver driver : availableDrivers) {
            if (excludeDriverIds.contains(driver.getId())) {
                continue;
            }
            double distance = ride.getPickupLocation().distanceTo(driver.getCurrentLocation());
            if (distance < minDistance) {
                minDistance = distance;
                nearestDriver = driver;
            }
        }
        if (nearestDriver == null) {
            System.out.println("No available drivers found for ride " + ride.getId());
            return Optional.empty();
        }
        return Optional.of(nearestDriver);
    }

}
