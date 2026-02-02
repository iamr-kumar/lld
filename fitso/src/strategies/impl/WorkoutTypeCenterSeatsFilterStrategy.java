package strategies.impl;

import models.WorkoutSlot;
import models.WorkoutType;
import strategies.SlotFilterStrategy;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy implementation for filtering by workout type and center,
 * sorting by available seats in ascending order.
 */
public class WorkoutTypeCenterSeatsFilterStrategy implements SlotFilterStrategy {

    private final WorkoutType workoutType;
    private final String centerId;

    public WorkoutTypeCenterSeatsFilterStrategy(WorkoutType workoutType, String centerId) {
        this.workoutType = workoutType;
        this.centerId = centerId;
    }

    @Override
    public List<WorkoutSlot> filterAndSort(List<WorkoutSlot> slots) {
        if (slots == null || workoutType == null || centerId == null) {
            return List.of();
        }

        return slots.stream()
                .filter(slot -> workoutType.equals(slot.getWorkoutType()))
                .filter(slot -> centerId.equals(slot.getCenterId()))
                .filter(WorkoutSlot::hasAvailableSeats) // Only show available slots
                .sorted(Comparator.comparingInt(WorkoutSlot::getAvailableSeats))
                .collect(Collectors.toList());
    }
}
