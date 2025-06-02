package parkinglot.src.fee;

public class BasicHourlyFeeStrategy implements ParkingFeeStrategy {
    private static final double BASE_FEE = 10.0;
    private static final double HOURLY_RATE = 5.0;

    @Override
    public double calculateFee(int hours) {
        if (hours <= 1) {
            return BASE_FEE; // No fee for non-positive hours
        }
        return BASE_FEE + (HOURLY_RATE * (hours - 1));
    }
}
