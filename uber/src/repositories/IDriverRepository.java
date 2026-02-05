package uber.src.repositories;

import java.util.List;

import uber.src.models.Driver;

public interface IDriverRepository {
    public void addDriver(Driver driver);

    public List<Driver> getAvailableDrivers();

    public List<Driver> getAllDrivers();
}
