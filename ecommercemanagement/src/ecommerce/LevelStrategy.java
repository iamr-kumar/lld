package ecommerce;

public interface LevelStrategy {
    double calculatePoints(double amount);

    double getMaxRedeemAmount(double amount);

    // double getDiscount(User user, double netAmount);
    Level getLevelType();
}
