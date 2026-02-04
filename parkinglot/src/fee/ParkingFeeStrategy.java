package parkinglot.src.fee;

public interface ParkingFeeStrategy {
    /**
     * Calculates the parking fee based on the duration of stay.
     *
     * @param hours The number of hours the vehicle was parked.
     * @return The total parking fee.
     */
    double calculateFee(int hours);
}
