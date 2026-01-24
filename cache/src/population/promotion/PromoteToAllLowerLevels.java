package population.promotion;

import java.util.HashSet;
import java.util.Set;

import population.IPopulationStrategy;

public class PromoteToAllLowerLevels implements IPopulationStrategy {
    @Override
    public Set<Integer> targetLevels(int totalLevels, int currentLevel) {
        Set<Integer> levels = new HashSet<>();
        for (int i = 0; i < totalLevels; i++) {
            if (i < currentLevel) {
                levels.add(i);
            }
        }
        return levels;
    }
}
