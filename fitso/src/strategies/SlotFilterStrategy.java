package fitso.src.strategies;

import fitso.src.models.WorkoutSlot;
import java.util.List;

/**
 * Strategy interface for filtering and sorting workout slots.
 * Implements Strategy pattern for different filtering criteria.
 */
public interface SlotFilterStrategy {

    /**
     * Filters and sorts workout slots based on specific criteria.
     * 
     * @param slots the list of workout slots to filter and sort
     * @return filtered and sorted list of workout slots
     */
    List<WorkoutSlot> filterAndSort(List<WorkoutSlot> slots);
}
