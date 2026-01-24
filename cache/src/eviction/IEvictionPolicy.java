package eviction;

public interface IEvictionPolicy<K> {
    void onAccess(K key);

    void onInsert(K key);

    void onRemove(K key);

    K evict();
}