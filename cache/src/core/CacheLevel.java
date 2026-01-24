package core;

import java.util.concurrent.ConcurrentHashMap;

import eviction.IEvictionPolicy;

/**
 * Operations are not thread safe on their own.
 * Caller should ensure thread safety if needed.
 */
public class CacheLevel<K, V> implements ICacheLevel<K, V> {
    private final int capacity;
    private final ConcurrentHashMap<K, V> cacheMap;
    private final IEvictionPolicy<K> evictionPolicy;

    public CacheLevel(int capacity, IEvictionPolicy<K> evictionPolicy) {
        this.capacity = capacity;
        this.cacheMap = new ConcurrentHashMap<>();
        this.evictionPolicy = evictionPolicy;
    }

    public V get(K key) {
        V value = cacheMap.get(key);
        if (value != null) {
            evictionPolicy.onAccess(key);
        }
        return value;
    }

    public void put(K key, V value) {
        if (cacheMap.containsKey(key)) {
            cacheMap.put(key, value);
            evictionPolicy.onAccess(key);
            return;
        }
        if (cacheMap.size() >= capacity) {
            K evictedKey = evictionPolicy.evict();
            if (evictedKey != null) {
                cacheMap.remove(evictedKey);
            }
        }
        cacheMap.put(key, value);
        evictionPolicy.onInsert(key);

    }

    public void remove(K key) {
        if (cacheMap.containsKey(key)) {
            cacheMap.remove(key);
            evictionPolicy.onRemove(key);
        }
    }

}
