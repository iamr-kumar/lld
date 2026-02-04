package trainscheduling.src.manager;

import trainscheduling.src.models.PlatformState;

public interface IPlatformAvailabilityManager {
    public PlatformState getNextAvailablePlatform();

    public void updatePlatformAvailability(PlatformState updatedPlatformState);
}
