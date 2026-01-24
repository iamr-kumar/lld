# Multi-Level Cache System - Low Level Design

## Problem Statement

Design a **multi-level caching system** that supports multiple cache layers (L1, L2, L3, etc.) with the following requirements:

### Functional Requirements

1. Support `get(key)` operation that searches through cache levels sequentially
2. Support `put(key, value)` operation to store data in cache
3. Support `remove(key)` operation to delete data from all cache levels
4. Each cache level should have a configurable capacity
5. Implement **eviction policy** (e.g., LRU) when cache is full
6. Implement **promotion strategy** - when data is found in a lower level (L2, L3), promote it to higher levels (L1)
7. Support different eviction policies per cache level

### Non-Functional Requirements

1. Thread-safe operations for concurrent access
2. Extensible design for adding new eviction policies
3. Configurable number of cache levels

---

## Design Overview

![Multi-Level Cache Design](multi-level-cache-design.png)

### Design Patterns Used

| Pattern                   | Where Used                               | Purpose                                                                                             |
| ------------------------- | ---------------------------------------- | --------------------------------------------------------------------------------------------------- |
| **Strategy Pattern**      | `IEvictionPolicy`, `IPopulationStrategy` | Allows swapping eviction algorithms (LRU, LFU) and promotion strategies without changing core logic |
| **Builder Pattern**       | `CacheBuilder`                           | Provides fluent API for constructing complex `MultiLevelCache` with validation                      |
| **Interface Segregation** | `ICacheLevel`, `IEvictionPolicy`         | Decouples implementations from concrete classes                                                     |

### SOLID Principles Applied

- **Single Responsibility**: `CacheLevel` handles storage, `LRUEvictionPolicy` handles eviction logic
- **Open/Closed**: New eviction policies can be added without modifying existing code
- **Liskov Substitution**: Any `IEvictionPolicy` implementation can be used interchangeably
- **Interface Segregation**: Small, focused interfaces (`ICacheLevel`, `IEvictionPolicy`)
- **Dependency Inversion**: `CacheLevel` depends on `IEvictionPolicy` abstraction, not concrete implementation

---

## Folder Structure

```
cache/
├── README.md
├── design.png
├── evaluation.md
└── src/
    ├── CacheMain.java                 # Driver class for testing
    ├── builder/
    │   └── CacheBuilder.java          # Builder pattern for cache construction
    ├── core/
    │   ├── ICacheLevel.java           # Interface for cache level abstraction
    │   ├── CacheLevel.java            # Single cache level implementation
    │   └── MultiLevelCache.java       # Orchestrates multiple cache levels
    ├── eviction/
    │   ├── IEvictionPolicy.java       # Strategy interface for eviction
    │   └── lru/
    │       └── LRUEvictionPolicy.java # LRU implementation using DLL + HashMap
    └── population/
        ├── IPopulationStrategy.java   # Strategy interface for promotion/demotion
        └── promotion/
            └── PromoteToAllLowerLevels.java  # Promotes to all higher-priority levels
```

---

## Key Classes

### 1. MultiLevelCache

The main orchestrator that manages multiple cache levels.

```java
public class MultiLevelCache<K, V> {
    private final List<ICacheLevel<K, V>> levels;
    private final IPopulationStrategy promotionStrategy;
    private final ReadWriteLock rwLock;

    public V get(K key);      // Search levels, promote on hit
    public void put(K key, V value);  // Write to all levels
    public void remove(K key);        // Remove from all levels
}
```

**Key Responsibilities:**

- Sequential lookup through cache levels (L1 → L2 → L3)
- Promotion of data to faster levels on cache hit
- Thread-safe operations using `ReentrantReadWriteLock`

---

### 2. CacheLevel

Represents a single cache layer with configurable capacity and eviction policy.

```java
public class CacheLevel<K, V> implements ICacheLevel<K, V> {
    private final int capacity;
    private final ConcurrentHashMap<K, V> cacheMap;
    private final IEvictionPolicy<K> evictionPolicy;

    public V get(K key);
    public void put(K key, V value);
    public void remove(K key);
}
```

**Key Responsibilities:**

- Store key-value pairs up to capacity
- Coordinate with eviction policy on access/insert/remove
- Trigger eviction when capacity is exceeded

---

### 3. IEvictionPolicy (Strategy Interface)

```java
public interface IEvictionPolicy<K> {
    void onAccess(K key);   // Called when key is accessed
    void onInsert(K key);   // Called when new key is inserted
    void onRemove(K key);   // Called when key is removed
    K evict();              // Returns key to evict
}
```

---

### 4. LRUEvictionPolicy

Implements Least Recently Used eviction using **Doubly Linked List + HashMap** for O(1) operations.

```
┌──────────────────────────────────────────────┐
│  HashMap<K, Node>  →  O(1) lookup            │
│  DoublyLinkedList  →  O(1) add/remove        │
│                                              │
│  HEAD ←→ [Most Recent] ←→ ... ←→ [LRU] ←→ TAIL
│                                      ↑        │
│                                   Evict this  │
└──────────────────────────────────────────────┘
```

---

### 5. IPopulationStrategy (Strategy Interface)

```java
public interface IPopulationStrategy {
    Set<Integer> targetLevels(int totalLevels, int currentLevel);
}
```

**Implementations:**

- `PromoteToAllLowerLevels`: When data found in L2, copy to L1

---

### 6. CacheBuilder

Fluent builder for constructing `MultiLevelCache`.

```java
MultiLevelCache<String, String> cache = new CacheBuilder<String, String>()
    .setLevels(3)
    .addCacheLevel(new CacheLevel<>(100, new LRUEvictionPolicy<>()))
    .addCacheLevel(new CacheLevel<>(500, new LRUEvictionPolicy<>()))
    .addCacheLevel(new CacheLevel<>(1000, new LRUEvictionPolicy<>()))
    .setPromotionStrategy(new PromoteToAllLowerLevels())
    .build();
```

---

## How It Works

### Get Operation

```
get("X")
   │
   ▼
┌─────┐    miss    ┌─────┐    miss    ┌─────┐
│ L1  │ ────────► │ L2  │ ────────► │ L3  │
└─────┘           └─────┘           └─────┘
                      │ hit
                      ▼
              promote("X") to L1
                      │
                      ▼
                return value
```

### Put Operation

```
put("X", value)
       │
       ▼
   ┌───────────────────────────┐
   │  Write to ALL levels      │
   │  L1 ← L2 ← L3            │
   │  (with eviction if full)  │
   └───────────────────────────┘
```

---

## Thread Safety

Locking is handled at the `MultiLevelCache` level using `ReentrantReadWriteLock`:

```java
public V get(K key) {
    rwLock.writeLock().lock();  // Write lock because promotion mutates state
    try {
        // ... search and promote
    } finally {
        rwLock.writeLock().unlock();
    }
}
```

**Design Decision:** Locking at the top level (MultiLevelCache) rather than individual CacheLevel to ensure atomic cross-level operations like promotion.

---

## Extensibility

### Adding a New Eviction Policy (e.g., LFU)

```java
public class LFUEvictionPolicy<K> implements IEvictionPolicy<K> {
    @Override
    public void onAccess(K key) { /* increment frequency */ }

    @Override
    public void onInsert(K key) { /* add with frequency 1 */ }

    @Override
    public void onRemove(K key) { /* remove from tracking */ }

    @Override
    public K evict() { /* return least frequently used key */ }
}
```

### Adding a New Promotion Strategy

```java
public class PromoteToL1Only implements IPopulationStrategy {
    @Override
    public Set<Integer> targetLevels(int totalLevels, int currentLevel) {
        return currentLevel > 0 ? Set.of(0) : Set.of();
    }
}
```

---

## Usage Example

```java
public static void main(String[] args) {
    // Build a 3-level cache
    CacheBuilder<String, String> builder = new CacheBuilder<>();
    builder.setLevels(3)
           .setPromotionStrategy(new PromoteToAllLowerLevels());

    // L1: 10 items, L2: 50 items, L3: 100 items
    builder.addCacheLevel(new CacheLevel<>(10, new LRUEvictionPolicy<>()));
    builder.addCacheLevel(new CacheLevel<>(50, new LRUEvictionPolicy<>()));
    builder.addCacheLevel(new CacheLevel<>(100, new LRUEvictionPolicy<>()));

    MultiLevelCache<String, String> cache = builder.build();

    // Use the cache
    cache.put("user:1", "Alice");
    String user = cache.get("user:1");  // Returns "Alice"
    cache.remove("user:1");
}
```

---

## Possible Improvements

1. **Write Strategy**: Add `IWriteStrategy` to control which levels receive writes
2. **TTL Support**: Add time-based expiration for cache entries
3. **Metrics**: Add hit/miss counters for monitoring
4. **Async Promotion**: Promote asynchronously to avoid blocking reads
5. **Demotion Strategy**: Move less-used data to slower levels
