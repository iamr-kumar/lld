package multigateparklinglot.src.strategy.fee;

public class HourlyFeeCalculation implements FeeCalculationStrategy {

    private static final double MINIMUM_FEE = 10.0;
    private static final double HOURLY_RATE = 5.0;

    @Override
    public double calculateFee(int durationInMinutes) {
        double fee = MINIMUM_FEE + (durationInMinutes / 60.0) * HOURLY_RATE;
        return fee;
    }
}
