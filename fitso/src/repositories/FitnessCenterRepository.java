package repositories;

import models.FitnessCenter;
import models.WorkoutType;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for FitnessCenter data access operations.
 */
public interface FitnessCenterRepository {

    /**
     * Saves a fitness center to the repository.
     * 
     * @param center the fitness center to save
     * @return the saved fitness center
     */
    FitnessCenter save(FitnessCenter center);

    /**
     * Finds a fitness center by its unique ID.
     * 
     * @param centerId the center ID to search for
     * @return Optional containing the center if found, empty otherwise
     */
    Optional<FitnessCenter> findById(String centerId);

    /**
     * Finds all fitness centers that support a specific workout type.
     * 
     * @param workoutType the workout type to search for
     * @return list of centers supporting the workout type
     */
    List<FitnessCenter> findByWorkoutType(WorkoutType workoutType);

    /**
     * Finds all fitness centers.
     * 
     * @return list of all fitness centers
     */
    List<FitnessCenter> findAll();

    /**
     * Checks if a fitness center exists with the given ID.
     * 
     * @param centerId the center ID to check
     * @return true if center exists, false otherwise
     */
    boolean existsById(String centerId);

    /**
     * Deletes a fitness center by its ID.
     * 
     * @param centerId the center ID to delete
     * @return true if center was deleted, false if not found
     */
    boolean deleteById(String centerId);
}
