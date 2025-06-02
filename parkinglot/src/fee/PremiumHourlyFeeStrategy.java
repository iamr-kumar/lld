package parkinglot.src.fee;

public class PremiumHourlyFeeStrategy implements ParkingFeeStrategy {
    private static final double BASE_FEE = 20.0;
    private static final double HOURLY_RATE = 10.0;

    @Override
    public double calculateFee(int hours) {
        if (hours <= 1) {
            return BASE_FEE;
        }
        return BASE_FEE + (HOURLY_RATE * (hours - 1));
    }
}
