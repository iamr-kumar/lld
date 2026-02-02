package ecommerce;

public class LevelStrategyFactory {
    public static LevelStrategy getLevelStrategy(double points) {
        if (points >= 1000) {
            return new GoldStrategy();
        } else if (points >= 500) {
            return new SilverStrategy();
        } else {
            return new BronzeStrategy();
        }
    }
}
