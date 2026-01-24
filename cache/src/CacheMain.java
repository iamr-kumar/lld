// driver class to check multi level cache

import java.util.Scanner;

import builder.CacheBuilder;
import core.CacheLevel;
import core.MultiLevelCache;
import eviction.lru.LRUEvictionPolicy;
import population.promotion.PromoteToAllLowerLevels;

public class CacheMain {
    public static void main(String[] args) {
        System.out.println("----- Multi-Level Cache Test ----- ");
        int numLevels;
        int capacityPerLevel;

        // Take input for number of levels and capacity per level
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of cache levels: ");
        numLevels = scanner.nextInt();
        System.out.print("Enter capacity per cache level: ");
        capacityPerLevel = scanner.nextInt();
        scanner.close();

        // Initialize cache levels and multi-level cache
        CacheBuilder<String, String> builder = new CacheBuilder<>();
        builder.setLevels(numLevels);
        builder.setLevels(numLevels);
        builder.setPromotionStrategy(new PromoteToAllLowerLevels());
        for (int i = 0; i < numLevels; i++) {
            builder.addCacheLevel(
                    new CacheLevel<String, String>(capacityPerLevel, new LRUEvictionPolicy<String>()));
        }
        MultiLevelCache<String, String> multiLevelCache = builder.build();

        // Test put and get operations
        multiLevelCache.put("A", "Apple");
        multiLevelCache.put("B", "Banana");
        multiLevelCache.put("C", "Cherry");
        System.out.println("Get A: " + multiLevelCache.get("A")); // Should print Apple
        System.out.println("Get B: " + multiLevelCache.get("B")); // Should
        System.out.println("Get C: " + multiLevelCache.get("C")); // Should print Cherry

        multiLevelCache.put("D", "Date");
        System.out.println("Get D: " + multiLevelCache.get("D")); // Should
        System.out.println("Get A: " + multiLevelCache.get("A")); // Should print Apple or null based on eviction

    }
}
