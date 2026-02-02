package manager;

import java.time.LocalTime;
import java.util.PriorityQueue;

import models.PlatformState;
import services.IPlatformService;

public class PlatformAvailabilityManager implements IPlatformAvailabilityManager {
    private final PriorityQueue<PlatformState> platformQueue;
    private final IPlatformService platformService;

    public PlatformAvailabilityManager(IPlatformService platformService) {
        this.platformService = platformService;
        this.platformQueue = new PriorityQueue<>(
                (ps1, ps2) -> ps1.getNextAvailableTime().compareTo(ps2.getNextAvailableTime()));
        initializePlatformQueue(LocalTime.of(0, 0));
    }

    @Override
    public PlatformState getNextAvailablePlatform() {
        return platformQueue.poll();
    }

    @Override
    public void updatePlatformAvailability(PlatformState updatedPlatformState) {
        platformQueue.offer(updatedPlatformState);
    }

    private void initializePlatformQueue(LocalTime initialTime) {
        for (var platform : platformService.getPlatforms()) {
            platformQueue.offer(new PlatformState(platform, initialTime));
        }
    }
}