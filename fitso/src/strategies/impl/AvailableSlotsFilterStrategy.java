package strategies.impl;

import models.WorkoutSlot;
import strategies.SlotFilterStrategy;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy implementation for showing all available slots sorted by start time.
 */
public class AvailableSlotsFilterStrategy implements SlotFilterStrategy {

    @Override
    public List<WorkoutSlot> filterAndSort(List<WorkoutSlot> slots) {
        if (slots == null) {
            return List.of();
        }

        return slots.stream()
                .filter(WorkoutSlot::hasAvailableSeats)
                .sorted(Comparator.comparingInt(WorkoutSlot::getStartTime))
                .collect(Collectors.toList());
    }
}
