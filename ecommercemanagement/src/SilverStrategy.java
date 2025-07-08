package ecommercemanagement.src;

public class SilverStrategy implements LevelStrategy {
    public double calculatePoints(double amount) {
        return (amount / 100) * 12.5;
    }

    public double getMaxRedeemAmount(double amount) {
        return Math.min(amount * 0.10, 500.00);
    }

    public Level getLevelType() {
        return Level.SILVER;
    }
}
