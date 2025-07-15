package fitso.src.models;

/**
 * Enum representing different types of workouts available in fitness centers.
 * Using enum for type safety and easy extensibility.
 */
public enum WorkoutType {
    WEIGHTS("Weights"),
    CARDIO("Cardio"),
    YOGA("Yoga"),
    SWIMMING("Swimming"),
    PILATES("Pilates"),
    CROSSFIT("CrossFit"),
    ZUMBA("Zumba");

    private final String displayName;

    WorkoutType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
