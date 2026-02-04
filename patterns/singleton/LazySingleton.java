package patterns.singleton;

public class LazySingleton {
    public static LazySingleton instance;

    private LazySingleton() {
        // Initialization code here
    }

    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
}
