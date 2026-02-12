package multigateparklinglot.src.strategy.fee;

public interface FeeCalculationStrategy {
    double calculateFee(int durationInMinutes);
}
