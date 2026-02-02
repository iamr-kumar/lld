package models;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a workout slot in a fitness center.
 * Thread-safe design with atomic operations for booking management.
 */
public class WorkoutSlot {
    private final String slotId;
    private final String centerId;
    private final WorkoutType workoutType;
    private final int startTime; // Hour in 24-hour format
    private final int duration; // Duration in hours
    private final int totalCapacity;
    private final AtomicInteger availableSeats;

    public WorkoutSlot(String slotId, String centerId, WorkoutType workoutType,
            int startTime, int duration, int totalCapacity) {
        this.slotId = Objects.requireNonNull(slotId, "Slot ID cannot be null");
        this.centerId = Objects.requireNonNull(centerId, "Center ID cannot be null");
        this.workoutType = Objects.requireNonNull(workoutType, "Workout type cannot be null");

        if (startTime < 0 || startTime > 23) {
            throw new IllegalArgumentException("Start time must be between 0-23");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        if (totalCapacity <= 0) {
            throw new IllegalArgumentException("Total capacity must be positive");
        }

        this.startTime = startTime;
        this.duration = duration;
        this.totalCapacity = totalCapacity;
        this.availableSeats = new AtomicInteger(totalCapacity);
    }

    public String getSlotId() {
        return slotId;
    }

    public String getCenterId() {
        return centerId;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public int getEndTime() {
        return startTime + duration;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public int getAvailableSeats() {
        return availableSeats.get();
    }

    public int getBookedSeats() {
        return totalCapacity - availableSeats.get();
    }

    public boolean hasAvailableSeats() {
        return availableSeats.get() > 0;
    }

    /**
     * Attempts to book a seat in this slot.
     * Thread-safe operation using atomic compare-and-swap.
     * 
     * @return true if booking was successful, false if no seats available
     */
    public boolean bookSeat() {
        while (true) {
            int current = availableSeats.get();
            if (current <= 0) {
                return false; // No seats available
            }
            if (availableSeats.compareAndSet(current, current - 1)) {
                return true; // Successfully booked
            }
            // Retry if compareAndSet failed due to concurrent modification
        }
    }

    /**
     * Cancels a booking and frees up a seat.
     * Thread-safe operation using atomic increment.
     * 
     * @return true if cancellation was successful
     */
    public boolean cancelSeat() {
        while (true) {
            int current = availableSeats.get();
            if (current >= totalCapacity) {
                return false; // Already at full capacity
            }
            if (availableSeats.compareAndSet(current, current + 1)) {
                return true; // Successfully cancelled
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        WorkoutSlot that = (WorkoutSlot) o;
        return Objects.equals(slotId, that.slotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotId);
    }

    @Override
    public String toString() {
        return "WorkoutSlot{" +
                "slotId='" + slotId + '\'' +
                ", centerId='" + centerId + '\'' +
                ", workoutType=" + workoutType +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", availableSeats=" + availableSeats.get() +
                ", totalCapacity=" + totalCapacity +
                '}';
    }
}
