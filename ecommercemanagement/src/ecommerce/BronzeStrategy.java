package ecommerce;

public class BronzeStrategy implements LevelStrategy {
    public double calculatePoints(double amount) {
        return (amount / 100) * 10;
    }

    public double getMaxRedeemAmount(double amount) {
        return Math.min(amount * 0.05, 200.0);
    }

    public Level getLevelType() {
        return Level.BRONZE;
    }
}
