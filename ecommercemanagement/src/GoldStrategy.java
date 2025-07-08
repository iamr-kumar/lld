package ecommercemanagement.src;

public class GoldStrategy implements LevelStrategy {
    public double calculatePoints(double amount) {
        return (amount / 100) * 15;
    }

    public double getMaxRedeemAmount(double amount) {
        return Math.min(amount * 0.15, 1000.00);
    }

    public Level getLevelType() {
        return Level.GOLD;
    }
}
