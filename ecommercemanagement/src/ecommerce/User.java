package ecommerce;

public class User {
    String username;
    double points;
    int orders;
    double totalSpent;
    LevelStrategy levelStrategy;

    public User(String username) {
        this.username = username;
        this.points = 0;
        this.orders = 0;
        this.totalSpent = 0.0;
        this.levelStrategy = LevelStrategyFactory.getLevelStrategy(points);
    }

    public void updateLevel() {
        this.levelStrategy = LevelStrategyFactory.getLevelStrategy(points);
    }

    public String getUserLevel() {
        Level level = this.levelStrategy.getLevelType();
        return Level.getLevelName(level);
    }

    public double getPoints() {
        return this.points;
    }

    public void updateOrderCount() {
        this.orders++;
    }

    public void resetOrderCount() {
        this.orders = 0;
    }

    public void updateTotalSpent(double amount) {
        this.totalSpent += amount;
    }
}
