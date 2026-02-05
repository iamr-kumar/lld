package cache.src.core;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import cache.src.population.IPopulationStrategy;

public class MultiLevelCache<K, V> {
    private final List<ICacheLevel<K, V>> levels;
    private final IPopulationStrategy promotionStrategy;
    private final int MAX_STRIPES = 64;
    private final ReentrantLock[] stripes;

    public MultiLevelCache(List<ICacheLevel<K, V>> levels, IPopulationStrategy promotionStrategy) {
        this.levels = levels;
        this.promotionStrategy = promotionStrategy;
        this.stripes = new ReentrantLock[MAX_STRIPES];
        for (int i = 0; i < MAX_STRIPES; i++) {
            stripes[i] = new ReentrantLock();
        }
    }

    private ReentrantLock getLockForKey(K key) {
        int hash = Math.abs(key.hashCode());
        int index = hash % MAX_STRIPES;
        return stripes[index];
    }

    public V get(K key) {
        ReentrantLock lock = getLockForKey(key);
        lock.lock();
        try {
            // Double check L1
            V value = levels.get(0).get(key);
            if (value != null) {
                return value;
            }
            for (int i = 1; i < levels.size(); i++) {
                ICacheLevel<K, V> cache = levels.get(i);
                value = cache.get(key);
                if (value != null) {
                    // promote to lower levels if needed
                    promote(key, value, i);
                    return value;
                }
            }
        } finally {
            lock.unlock();
        }
        return null;
    }

    public void put(K key, V value) {
        ReentrantLock lock = getLockForKey(key);
        lock.lock();
        try {
            // put into all levels
            // We could introduce a strategy here as well
            // for deciding the write levels
            for (ICacheLevel<K, V> cache : levels) {
                cache.put(key, value);
            }
        } finally {
            lock.unlock();
        }
    }

    public void remove(K key) {
        ReentrantLock lock = getLockForKey(key);
        lock.lock();
        try {
            for (ICacheLevel<K, V> cache : levels) {
                cache.remove(key);
            }
        } finally {
            lock.unlock();
        }
    }

    private void promote(K key, V value, int currentLevel) {
        Set<Integer> targetLevels = promotionStrategy.targetLevels(levels.size(), currentLevel);
        for (int levelIndex : targetLevels) {
            ICacheLevel<K, V> cache = levels.get(levelIndex);
            cache.put(key, value);
        }
    }
}
