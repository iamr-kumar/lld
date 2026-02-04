package fitso.src.repositories;

import fitso.src.models.WorkoutSlot;
import fitso.src.models.WorkoutType;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for WorkoutSlot data access operations.
 */
public interface WorkoutSlotRepository {

    /**
     * Saves a workout slot to the repository.
     * 
     * @param slot the workout slot to save
     * @return the saved workout slot
     */
    WorkoutSlot save(WorkoutSlot slot);

    /**
     * Finds a workout slot by its unique ID.
     * 
     * @param slotId the slot ID to search for
     * @return Optional containing the slot if found, empty otherwise
     */
    Optional<WorkoutSlot> findById(String slotId);

    /**
     * Finds all workout slots for a specific center.
     * 
     * @param centerId the center ID to search for
     * @return list of workout slots for the center
     */
    List<WorkoutSlot> findByCenterId(String centerId);

    /**
     * Finds all workout slots by workout type.
     * 
     * @param workoutType the workout type to search for
     * @return list of workout slots for the workout type
     */
    List<WorkoutSlot> findByWorkoutType(WorkoutType workoutType);

    /**
     * Finds all workout slots by center and workout type.
     * 
     * @param centerId    the center ID
     * @param workoutType the workout type
     * @return list of matching workout slots
     */
    List<WorkoutSlot> findByCenterIdAndWorkoutType(String centerId, WorkoutType workoutType);

    /**
     * Finds all workout slots with available seats.
     * 
     * @return list of workout slots with available seats
     */
    List<WorkoutSlot> findAvailableSlots();

    /**
     * Finds all workout slots.
     * 
     * @return list of all workout slots
     */
    List<WorkoutSlot> findAll();

    /**
     * Checks if a workout slot exists with the given ID.
     * 
     * @param slotId the slot ID to check
     * @return true if slot exists, false otherwise
     */
    boolean existsById(String slotId);

    /**
     * Deletes a workout slot by its ID.
     * 
     * @param slotId the slot ID to delete
     * @return true if slot was deleted, false if not found
     */
    boolean deleteById(String slotId);
}
