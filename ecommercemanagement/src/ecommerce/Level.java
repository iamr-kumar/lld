package ecommerce;

public enum Level {
    BRONZE,
    SILVER,
    GOLD;

    public static String getLevelName(Level level) {
        switch (level) {
            case BRONZE:
                return "Bronze";
            case SILVER:
                return "Silver";
            case GOLD:
                return "Gold";
            default:
                throw new IllegalArgumentException("Unknown level: " + level);
        }
    }
}
