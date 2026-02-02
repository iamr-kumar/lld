package services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Platform;

public class PlatformService implements IPlatformService {
    List<Platform> platforms;

    public PlatformService() {
        this.platforms = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public List<Platform> getPlatforms() {
        return platforms;
    }

    @Override
    public int addPlatform(int platformNumber) {
        platforms.add(new Platform(platformNumber));
        return platformNumber;
    }
}
