package singleton;

/**
 * Static block singleton allows exception handling during instance creation.
 * Eager singleton does not allow exception handling.
 * Both create the instance at class loading time (they are not lazy).
 * Use getInstance() to access the singleton.
 */
public class StaticBlockSingleton {
    private static StaticBlockSingleton instance;

    private StaticBlockSingleton() {
        // Initialization code here
    }

    /**
     * Static blocks are commonly used to perform complex initialization of static
     * variables,
     * or to handle exceptions during static initialization (which you can’t do in a
     * simple assignment).
     */
    static {
        try {
            instance = new StaticBlockSingleton();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while creating singleton instance", e);
        }
    }

    public static StaticBlockSingleton getInstance() {
        return instance;
    }
}
