package fitso.src.services;

import fitso.src.models.FitnessCenter;
import fitso.src.models.WorkoutType;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface for FitnessCenter-related business operations.
 */
public interface FitnessCenterService {

    /**
     * Onboards a new fitness center to the platform.
     * 
     * @param centerId          unique identifier for the center
     * @param name              center name
     * @param openingHour       opening hour (0-23)
     * @param closingHour       closing hour (0-23)
     * @param slotsCapacity     number of seats per slot
     * @param supportedWorkouts set of supported workout types
     * @return the onboarded fitness center
     * @throws IllegalArgumentException if center already exists or invalid
     *                                  parameters
     */
    FitnessCenter onboardCenter(String centerId, String name, int openingHour,
            int closingHour, int slotsCapacity, Set<WorkoutType> supportedWorkouts);

    /**
     * Finds a fitness center by its ID.
     * 
     * @param centerId the center ID to search for
     * @return Optional containing the center if found, empty otherwise
     */
    Optional<FitnessCenter> findCenterById(String centerId);

    /**
     * Finds all fitness centers that support a specific workout type.
     * 
     * @param workoutType the workout type to search for
     * @return list of centers supporting the workout type
     */
    List<FitnessCenter> findCentersByWorkoutType(WorkoutType workoutType);

    /**
     * Gets all fitness centers in the system.
     * 
     * @return list of all fitness centers
     */
    List<FitnessCenter> getAllCenters();

    /**
     * Validates if a center exists and supports a specific workout type.
     * 
     * @param centerId    the center ID to validate
     * @param workoutType the workout type to check
     * @return true if center exists and supports the workout type, false otherwise
     */
    boolean validateCenterSupportsWorkout(String centerId, WorkoutType workoutType);
}
