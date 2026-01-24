package population;

import java.util.Set;

public interface IPopulationStrategy {
    Set<Integer> targetLevels(int totalLevels, int currentLevel);
}
