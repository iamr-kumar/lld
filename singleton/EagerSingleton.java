package singleton;

public class EagerSingleton {
    // Thread safe as it is initialized at class loading time
    private static final EagerSingleton INSTANCE = new EagerSingleton();

    // Private constructor to prevent instantiation
    private EagerSingleton() {
        // Initialization code here
    }

    public static EagerSingleton getInstance() {
        return INSTANCE;
    }
}
