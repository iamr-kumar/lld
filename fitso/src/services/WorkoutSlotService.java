package services;

import models.WorkoutSlot;
import models.WorkoutType;
import strategies.SlotFilterStrategy;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for WorkoutSlot-related business operations.
 */
public interface WorkoutSlotService {

    /**
     * Creates a new workout slot for a fitness center.
     * 
     * @param slotId      unique identifier for the slot
     * @param centerId    the center ID where the slot will be created
     * @param workoutType the type of workout for this slot
     * @param startTime   the start time of the slot (hour in 24-hour format)
     * @param duration    the duration of the workout in hours
     * @return the created workout slot
     * @throws IllegalArgumentException if center doesn't exist, doesn't support
     *                                  workout type,
     *                                  or slot time is outside operating hours
     */
    WorkoutSlot createWorkoutSlot(String slotId, String centerId, WorkoutType workoutType,
            int startTime, int duration);

    /**
     * Finds a workout slot by its ID.
     * 
     * @param slotId the slot ID to search for
     * @return Optional containing the slot if found, empty otherwise
     */
    Optional<WorkoutSlot> findSlotById(String slotId);

    /**
     * Gets all workout slots for a specific center.
     * 
     * @param centerId the center ID
     * @return list of workout slots for the center
     */
    List<WorkoutSlot> getSlotsByCenter(String centerId);

    /**
     * Gets workout slots filtered by workout type and sorted by start time.
     * 
     * @param workoutType the workout type to filter by
     * @return filtered and sorted list of workout slots
     */
    List<WorkoutSlot> getSlotsByWorkoutType(WorkoutType workoutType);

    /**
     * Gets workout slots filtered by workout type and center, sorted by available
     * seats.
     * 
     * @param workoutType the workout type to filter by
     * @param centerId    the center ID to filter by
     * @return filtered and sorted list of workout slots
     */
    List<WorkoutSlot> getSlotsByWorkoutTypeAndCenter(WorkoutType workoutType, String centerId);

    /**
     * Gets all available workout slots (with available seats).
     * 
     * @return list of available workout slots
     */
    List<WorkoutSlot> getAvailableSlots();

    /**
     * Gets workout slots using a custom filter strategy.
     * 
     * @param filterStrategy the strategy to use for filtering and sorting
     * @return filtered and sorted list of workout slots
     */
    List<WorkoutSlot> getSlotsWithFilter(SlotFilterStrategy filterStrategy);

    /**
     * Validates if a slot exists and has available seats.
     * 
     * @param slotId the slot ID to validate
     * @return true if slot exists and has available seats, false otherwise
     */
    boolean validateSlotAvailability(String slotId);
}
