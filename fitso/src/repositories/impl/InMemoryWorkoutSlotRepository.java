package repositories.impl;

import models.WorkoutSlot;
import models.WorkoutType;
import repositories.WorkoutSlotRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of WorkoutSlotRepository using ConcurrentHashMap for
 * thread safety.
 */
public class InMemoryWorkoutSlotRepository implements WorkoutSlotRepository {

    private final ConcurrentHashMap<String, WorkoutSlot> slots = new ConcurrentHashMap<>();

    @Override
    public WorkoutSlot save(WorkoutSlot slot) {
        if (slot == null) {
            throw new IllegalArgumentException("Workout slot cannot be null");
        }
        slots.put(slot.getSlotId(), slot);
        return slot;
    }

    @Override
    public Optional<WorkoutSlot> findById(String slotId) {
        if (slotId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(slots.get(slotId));
    }

    @Override
    public List<WorkoutSlot> findByCenterId(String centerId) {
        if (centerId == null) {
            return List.of();
        }

        return slots.values().stream()
                .filter(slot -> centerId.equals(slot.getCenterId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkoutSlot> findByWorkoutType(WorkoutType workoutType) {
        if (workoutType == null) {
            return List.of();
        }

        return slots.values().stream()
                .filter(slot -> workoutType.equals(slot.getWorkoutType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkoutSlot> findByCenterIdAndWorkoutType(String centerId, WorkoutType workoutType) {
        if (centerId == null || workoutType == null) {
            return List.of();
        }

        return slots.values().stream()
                .filter(slot -> centerId.equals(slot.getCenterId()) &&
                        workoutType.equals(slot.getWorkoutType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkoutSlot> findAvailableSlots() {
        return slots.values().stream()
                .filter(WorkoutSlot::hasAvailableSeats)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkoutSlot> findAll() {
        return List.copyOf(slots.values());
    }

    @Override
    public boolean existsById(String slotId) {
        return slotId != null && slots.containsKey(slotId);
    }

    @Override
    public boolean deleteById(String slotId) {
        return slotId != null && slots.remove(slotId) != null;
    }
}
