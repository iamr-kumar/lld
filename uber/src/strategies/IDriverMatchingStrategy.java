package uber.src.strategies;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import uber.src.models.Driver;
import uber.src.models.Ride;

public interface IDriverMatchingStrategy {
    public Optional<Driver> findDriver(Ride ride, List<Driver> availableDrivers, Set<String> excludeDriverIds);
}
