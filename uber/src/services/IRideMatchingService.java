package uber.src.services;

import java.util.Optional;

import uber.src.models.Driver;
import uber.src.models.Ride;

public interface IRideMatchingService {
    public Optional<Driver> findAndAssignDriver(Ride ride);
}
