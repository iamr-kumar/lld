package fitso.src.services.impl;

import fitso.src.models.FitnessCenter;
import fitso.src.models.WorkoutType;
import fitso.src.repositories.FitnessCenterRepository;
import fitso.src.services.FitnessCenterService;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of FitnessCenterService with business logic for fitness center
 * operations.
 */
public class FitnessCenterServiceImpl implements FitnessCenterService {

    private final FitnessCenterRepository centerRepository;

    public FitnessCenterServiceImpl(FitnessCenterRepository centerRepository) {
        this.centerRepository = centerRepository;
    }

    @Override
    public FitnessCenter onboardCenter(String centerId, String name, int openingHour,
            int closingHour, int slotsCapacity, Set<WorkoutType> supportedWorkouts) {
        // Validate input parameters
        if (centerId == null || centerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Center ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Center name cannot be null or empty");
        }
        if (supportedWorkouts == null || supportedWorkouts.isEmpty()) {
            throw new IllegalArgumentException("Supported workouts cannot be null or empty");
        }

        // Check if center already exists
        if (centerRepository.existsById(centerId)) {
            throw new IllegalArgumentException("Fitness center already exists with ID: " + centerId);
        }

        // Create and save fitness center
        FitnessCenter center = new FitnessCenter(centerId.trim(), name.trim(), openingHour,
                closingHour, slotsCapacity, supportedWorkouts);
        return centerRepository.save(center);
    }

    @Override
    public Optional<FitnessCenter> findCenterById(String centerId) {
        return centerRepository.findById(centerId);
    }

    @Override
    public List<FitnessCenter> findCentersByWorkoutType(WorkoutType workoutType) {
        if (workoutType == null) {
            throw new IllegalArgumentException("Workout type cannot be null");
        }
        return centerRepository.findByWorkoutType(workoutType);
    }

    @Override
    public List<FitnessCenter> getAllCenters() {
        return centerRepository.findAll();
    }

    @Override
    public boolean validateCenterSupportsWorkout(String centerId, WorkoutType workoutType) {
        if (centerId == null || workoutType == null) {
            return false;
        }

        Optional<FitnessCenter> centerOpt = centerRepository.findById(centerId);
        return centerOpt.map(center -> center.supportsWorkout(workoutType)).orElse(false);
    }
}
