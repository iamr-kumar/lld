package services;

import java.util.List;

import models.Platform;

public interface IPlatformService {
    public List<Platform> getPlatforms();

    public int addPlatform(int platformNumber);
}