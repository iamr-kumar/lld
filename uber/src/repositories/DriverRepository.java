package uber.src.repositories;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import uber.src.models.Driver;

public class DriverRepository implements IDriverRepository {
    private final ConcurrentHashMap<String, Driver> drivers;

    public DriverRepository() {
        this.drivers = new ConcurrentHashMap<>();
    }

    @Override
    public void addDriver(Driver driver) {
        drivers.put(driver.getId(), driver);
    }

    @Override
    public List<Driver> getAvailableDrivers() {
        return drivers.values().stream().filter(driver -> driver.isAvailable()).toList();
    }

    @Override
    public List<Driver> getAllDrivers() {
        return drivers.values().stream().toList();
    }
}
