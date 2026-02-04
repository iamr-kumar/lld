package fitso.src.services.impl;

import fitso.src.models.FitnessCenter;
import fitso.src.models.WorkoutSlot;
import fitso.src.models.WorkoutType;
import fitso.src.repositories.FitnessCenterRepository;
import fitso.src.repositories.WorkoutSlotRepository;
import fitso.src.services.WorkoutSlotService;
import fitso.src.strategies.SlotFilterStrategy;
import fitso.src.strategies.impl.AvailableSlotsFilterStrategy;
import fitso.src.strategies.impl.WorkoutTypeCenterSeatsFilterStrategy;
import fitso.src.strategies.impl.WorkoutTypeTimeFilterStrategy;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of WorkoutSlotService with business logic for workout slot
 * operations.
 */
public class WorkoutSlotServiceImpl implements WorkoutSlotService {

    private final WorkoutSlotRepository slotRepository;
    private final FitnessCenterRepository centerRepository;

    public WorkoutSlotServiceImpl(WorkoutSlotRepository slotRepository,
            FitnessCenterRepository centerRepository) {
        this.slotRepository = slotRepository;
        this.centerRepository = centerRepository;
    }

    @Override
    public WorkoutSlot createWorkoutSlot(String slotId, String centerId, WorkoutType workoutType,
            int startTime, int duration) {
        // Validate input parameters
        if (slotId == null || slotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Slot ID cannot be null or empty");
        }
        if (centerId == null || centerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Center ID cannot be null or empty");
        }
        if (workoutType == null) {
            throw new IllegalArgumentException("Workout type cannot be null");
        }

        // Check if slot already exists
        if (slotRepository.existsById(slotId)) {
            throw new IllegalArgumentException("Workout slot already exists with ID: " + slotId);
        }

        // Validate fitness center
        Optional<FitnessCenter> centerOpt = centerRepository.findById(centerId);
        if (centerOpt.isEmpty()) {
            throw new IllegalArgumentException("Fitness center not found: " + centerId);
        }

        FitnessCenter center = centerOpt.get();

        // Check if center supports this workout type
        if (!center.supportsWorkout(workoutType)) {
            throw new IllegalArgumentException("Center " + centerId + " does not support workout type: " + workoutType);
        }

        // Check if slot time is within operating hours
        if (!center.isOperatingAt(startTime)) {
            throw new IllegalArgumentException("Slot start time " + startTime +
                    " is outside center operating hours (" + center.getOpeningHour() + "-" + center.getClosingHour()
                    + ")");
        }

        // Check if slot end time is within operating hours
        int endTime = startTime + duration;
        if (endTime > center.getClosingHour()) {
            throw new IllegalArgumentException("Slot end time " + endTime +
                    " is outside center operating hours (" + center.getOpeningHour() + "-" + center.getClosingHour()
                    + ")");
        }

        // Create and save workout slot
        WorkoutSlot slot = new WorkoutSlot(slotId.trim(), centerId, workoutType,
                startTime, duration, center.getSlotsCapacity());
        return slotRepository.save(slot);
    }

    @Override
    public Optional<WorkoutSlot> findSlotById(String slotId) {
        return slotRepository.findById(slotId);
    }

    @Override
    public List<WorkoutSlot> getSlotsByCenter(String centerId) {
        if (centerId == null || centerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Center ID cannot be null or empty");
        }
        return slotRepository.findByCenterId(centerId);
    }

    @Override
    public List<WorkoutSlot> getSlotsByWorkoutType(WorkoutType workoutType) {
        if (workoutType == null) {
            throw new IllegalArgumentException("Workout type cannot be null");
        }

        SlotFilterStrategy strategy = new WorkoutTypeTimeFilterStrategy(workoutType);
        return strategy.filterAndSort(slotRepository.findAll());
    }

    @Override
    public List<WorkoutSlot> getSlotsByWorkoutTypeAndCenter(WorkoutType workoutType, String centerId) {
        if (workoutType == null) {
            throw new IllegalArgumentException("Workout type cannot be null");
        }
        if (centerId == null || centerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Center ID cannot be null or empty");
        }

        SlotFilterStrategy strategy = new WorkoutTypeCenterSeatsFilterStrategy(workoutType, centerId);
        return strategy.filterAndSort(slotRepository.findAll());
    }

    @Override
    public List<WorkoutSlot> getAvailableSlots() {
        SlotFilterStrategy strategy = new AvailableSlotsFilterStrategy();
        return strategy.filterAndSort(slotRepository.findAll());
    }

    @Override
    public List<WorkoutSlot> getSlotsWithFilter(SlotFilterStrategy filterStrategy) {
        if (filterStrategy == null) {
            throw new IllegalArgumentException("Filter strategy cannot be null");
        }
        return filterStrategy.filterAndSort(slotRepository.findAll());
    }

    @Override
    public boolean validateSlotAvailability(String slotId) {
        if (slotId == null) {
            return false;
        }

        Optional<WorkoutSlot> slotOpt = slotRepository.findById(slotId);
        return slotOpt.map(WorkoutSlot::hasAvailableSeats).orElse(false);
    }
}
