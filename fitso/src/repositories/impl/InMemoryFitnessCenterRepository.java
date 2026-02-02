package repositories.impl;

import models.FitnessCenter;
import models.WorkoutType;
import repositories.FitnessCenterRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of FitnessCenterRepository using ConcurrentHashMap
 * for thread safety.
 */
public class InMemoryFitnessCenterRepository implements FitnessCenterRepository {

    private final ConcurrentHashMap<String, FitnessCenter> centers = new ConcurrentHashMap<>();

    @Override
    public FitnessCenter save(FitnessCenter center) {
        if (center == null) {
            throw new IllegalArgumentException("Fitness center cannot be null");
        }
        centers.put(center.getCenterId(), center);
        return center;
    }

    @Override
    public Optional<FitnessCenter> findById(String centerId) {
        if (centerId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(centers.get(centerId));
    }

    @Override
    public List<FitnessCenter> findByWorkoutType(WorkoutType workoutType) {
        if (workoutType == null) {
            return List.of();
        }

        return centers.values().stream()
                .filter(center -> center.supportsWorkout(workoutType))
                .collect(Collectors.toList());
    }

    @Override
    public List<FitnessCenter> findAll() {
        return List.copyOf(centers.values());
    }

    @Override
    public boolean existsById(String centerId) {
        return centerId != null && centers.containsKey(centerId);
    }

    @Override
    public boolean deleteById(String centerId) {
        return centerId != null && centers.remove(centerId) != null;
    }
}
