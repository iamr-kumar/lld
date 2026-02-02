package strategies.impl;

import models.WorkoutSlot;
import models.WorkoutType;
import strategies.SlotFilterStrategy;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy implementation for filtering by workout type and sorting by start
 * time.
 */
public class WorkoutTypeTimeFilterStrategy implements SlotFilterStrategy {

    private final WorkoutType workoutType;

    public WorkoutTypeTimeFilterStrategy(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    @Override
    public List<WorkoutSlot> filterAndSort(List<WorkoutSlot> slots) {
        if (slots == null || workoutType == null) {
            return List.of();
        }

        return slots.stream()
                .filter(slot -> workoutType.equals(slot.getWorkoutType()))
                .filter(WorkoutSlot::hasAvailableSeats) // Only show available slots
                .sorted(Comparator.comparingInt(WorkoutSlot::getStartTime))
                .collect(Collectors.toList());
    }
}
