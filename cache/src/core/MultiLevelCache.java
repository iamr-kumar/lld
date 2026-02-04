package cache.src.core;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cache.src.population.IPopulationStrategy;

public class MultiLevelCache<K, V> {
    private final List<ICacheLevel<K, V>> levels;
    private final IPopulationStrategy promotionStrategy;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public MultiLevelCache(List<ICacheLevel<K, V>> levels, IPopulationStrategy promotionStrategy) {
        this.levels = levels;
        this.promotionStrategy = promotionStrategy;
    }

    public V get(K key) {
        rwLock.writeLock().lock();
        try {
            for (int i = 0; i < levels.size(); i++) {
                ICacheLevel<K, V> cache = levels.get(i);
                V value = cache.get(key);
                if (value != null) {
                    // promote to lower levels if needed
                    promote(key, value, i);
                    return value;
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }
        return null;
    }

    public void put(K key, V value) {
        rwLock.writeLock().lock();
        try {
            // put into all levels
            // We could introduce a strategy here as well
            // for deciding the write levels
            for (ICacheLevel<K, V> cache : levels) {
                cache.put(key, value);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void remove(K key) {
        rwLock.writeLock().lock();
        try {
            for (ICacheLevel<K, V> cache : levels) {
                cache.remove(key);
            }
        } finally {
            rwLock.writeLock().unlock();
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
