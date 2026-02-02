package models;

import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

/**
 * Represents a fitness center in the system.
 * Encapsulates center details and supported workout types.
 */
public class FitnessCenter {
    private final String centerId;
    private final String name;
    private final int openingHour; // 24-hour format
    private final int closingHour; // 24-hour format
    private final int slotsCapacity; // Fixed seats per slot for this center
    private final Set<WorkoutType> supportedWorkouts;

    public FitnessCenter(String centerId, String name, int openingHour, int closingHour,
            int slotsCapacity, Set<WorkoutType> supportedWorkouts) {
        this.centerId = Objects.requireNonNull(centerId, "Center ID cannot be null");
        this.name = Objects.requireNonNull(name, "Center name cannot be null");

        if (openingHour < 0 || openingHour > 23 || closingHour < 0 || closingHour > 23) {
            throw new IllegalArgumentException("Hours must be between 0-23");
        }
        if (openingHour >= closingHour) {
            throw new IllegalArgumentException("Opening hour must be before closing hour");
        }
        if (slotsCapacity <= 0) {
            throw new IllegalArgumentException("Slots capacity must be positive");
        }

        this.openingHour = openingHour;
        this.closingHour = closingHour;
        this.slotsCapacity = slotsCapacity;
        this.supportedWorkouts = new HashSet<>(Objects.requireNonNull(supportedWorkouts,
                "Supported workouts cannot be null"));
    }

    public String getCenterId() {
        return centerId;
    }

    public String getName() {
        return name;
    }

    public int getOpeningHour() {
        return openingHour;
    }

    public int getClosingHour() {
        return closingHour;
    }

    public int getSlotsCapacity() {
        return slotsCapacity;
    }

    public Set<WorkoutType> getSupportedWorkouts() {
        return new HashSet<>(supportedWorkouts);
    }

    public boolean supportsWorkout(WorkoutType workoutType) {
        return supportedWorkouts.contains(workoutType);
    }

    public boolean isOperatingAt(int hour) {
        return hour >= openingHour && hour < closingHour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FitnessCenter that = (FitnessCenter) o;
        return Objects.equals(centerId, that.centerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(centerId);
    }

    @Override
    public String toString() {
        return "FitnessCenter{" +
                "centerId='" + centerId + '\'' +
                ", name='" + name + '\'' +
                ", openingHour=" + openingHour +
                ", closingHour=" + closingHour +
                ", slotsCapacity=" + slotsCapacity +
                ", supportedWorkouts=" + supportedWorkouts +
                '}';
    }
}
