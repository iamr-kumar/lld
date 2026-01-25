package singleton;

public class LazyThreadSafeSingleton {
    public static LazyThreadSafeSingleton instance;

    private LazyThreadSafeSingleton() {
        // Initialization code here
    }

    public static synchronized LazyThreadSafeSingleton getInstance() {
        if (instance == null) {
            instance = new LazyThreadSafeSingleton();
        }
        return instance;
    }
}
