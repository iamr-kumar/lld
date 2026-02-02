package manager;

import models.PlatformState;

public interface IPlatformAvailabilityManager {
    public PlatformState getNextAvailablePlatform();

    public void updatePlatformAvailability(PlatformState updatedPlatformState);
}
