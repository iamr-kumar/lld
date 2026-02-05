package uber.src.services;

import uber.src.enums.DriverResponse;
import uber.src.models.Driver;
import uber.src.models.Ride;

public interface IDriverNotificationService {
    public DriverResponse sendRequestToDriverAndWaitForResponse(Driver driver, Ride request);

    public void shutdown();
}
