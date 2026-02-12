package multigateparklinglot.src.factory;

import multigateparklinglot.src.enums.FeeType;
import multigateparklinglot.src.strategy.fee.FeeCalculationStrategy;
import multigateparklinglot.src.strategy.fee.HourlyFeeCalculation;

public class FeeFactory {

    public static FeeCalculationStrategy getFeeCalculationStrategy(FeeType type) {
        switch (type) {
            case HOURLY:
                return new HourlyFeeCalculation();
            default:
                return new HourlyFeeCalculation();
        }
    }
}
