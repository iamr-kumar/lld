package trainscheduling.src.services;

import java.util.List;

import trainscheduling.src.models.Platform;

public interface IPlatformService {
    public List<Platform> getPlatforms();

    public int addPlatform(int platformNumber);
}