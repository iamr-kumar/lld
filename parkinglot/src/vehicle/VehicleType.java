package parkinglot.src.vehicle;

public enum VehicleType {
    CAR,
    BIKE,
    OTHERS;

    public static VehicleType fromString(String type) {
        switch (type.toUpperCase()) {
            case "CAR":
                return CAR;
            case "BIKE":
                return BIKE;
            default:
                return OTHERS;
        }
    }
}
