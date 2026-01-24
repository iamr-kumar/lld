# Kafka-like Producer-Consumer System - Code Review

## Overview

This is a review of a simplified Kafka-like message broker system implementation covering SOLID principles, design patterns, functionality, and concurrency handling.

---

## 1. SOLID Principles Analysis

### ✅ Single Responsibility Principle (SRP)

**Good:**

- `Message` class is focused solely on holding message data
- `Partition` class handles partition-level storage
- Strategy classes (`RoundRobinStrategy`, `HashedPartitionStrategy`) have single focused responsibilities

**Issues:**

- `Topic.AddMessage()` violates SRP - it calculates the offset AND creates the message AND appends to partition. Offset management should be partition's responsibility.
- `ConsumerGroup` does too much: manages consumers, handles subscriptions, tracks offsets, performs rebalancing, and polls messages.

### ✅ Open/Closed Principle (OCP)

**Good:**

- `IPartitionStrategy` interface allows adding new partition strategies without modifying existing code
- `IDistributionStrategy` interface allows new rebalancing strategies

**Issues:**

- None significant

### ✅ Liskov Substitution Principle (LSP)

**Good:**

- Strategy implementations can be substituted without breaking behavior

**Issues:**

- None significant

### ✅ Interface Segregation Principle (ISP)

**Good:**

- `IPartitionStrategy` and `IDistributionStrategy` are small, focused interfaces

**Issues:**

- None significant

### ❌ Dependency Inversion Principle (DIP)

**Issues:**

- `ConsumerGroup` directly instantiates `RoundRobinDistribution` instead of receiving it via dependency injection:
  ```java
  this.distributionStrategy = new RoundRobinDistribution(); // Hardcoded dependency
  ```
- `Broker` creates `Topic` and `ConsumerGroup` directly instead of using factories or injection

---

## 2. Design Patterns

### ✅ Patterns Used Correctly

1. **Strategy Pattern** - Used for partition selection (`IPartitionStrategy`) and consumer distribution (`IDistributionStrategy`)

### ❌ Patterns Issues

1. **Strategy Pattern (Partial Violation)** - `ConsumerGroup` hardcodes `RoundRobinDistribution` instead of accepting it via constructor

---

## 3. Concurrency Issues (Critical)

### 🔴 Critical Bug #1: Race Condition in Offset Calculation (Topic.java)

```java
public int AddMessage(String key, String message) {
    int partitionIndex = partitionStrategy.getPartition(key, topicPartitions.size());
    String partitionId = name + "-part-" + partitionIndex;
    Partition partition = topicPartitions.get(partitionId);
    Optional<AtomicInteger> lastOffset = partition.getLastCommittedOffset();
    int newOffset = lastOffset.isPresent() ? lastOffset.get().incrementAndGet() : 0;
    // ⚠️ RACE CONDITION: Another thread can get same offset here before append
    Message msg = new Message(message, newOffset);
    return partition.appendMessage(msg);
}
```

**Problem:** Between getting the last offset and appending the message, another thread could do the same, resulting in duplicate offsets or lost messages.

**Fix:** Move offset generation into `Partition.appendMessage()` using atomic operations:

```java
// In Partition.java
private AtomicInteger offsetCounter = new AtomicInteger(0);

public int appendMessage(Message message) {
    int offset = offsetCounter.getAndIncrement();
    Message msg = new Message(message.getMessage(), offset);
    messages.put(offset, msg);
    return offset;
}
```

### 🔴 Critical Bug #2: Unused ReadWriteLock (Partition.java)

```java
ReadWriteLock rwLock = new ReentrantReadWriteLock(); // Declared but NEVER used
```

**Problem:** Lock is declared but never acquired/released. Methods like `appendMessage()` and `getMessageAtOffset()` are not thread-safe despite the intent.

**Fix:** Either use the lock properly or remove it since `ConcurrentHashMap` provides basic thread-safety:

```java
public int appendMessage(Message message) {
    rwLock.writeLock().lock();
    try {
        // write operations
    } finally {
        rwLock.writeLock().unlock();
    }
}
```

### 🔴 Critical Bug #3: Non-Thread-Safe Collections in ConsumerGroup

```java
List<String> subscribedTopics = new ArrayList<>();      // Not thread-safe
List<Partition> allPartitions = new ArrayList<>();       // Not thread-safe
```

**Problem:** These collections can be modified by multiple threads (subscribe, rebalance) without synchronization.

**Fix:** Use thread-safe alternatives:

```java
List<String> subscribedTopics = Collections.synchronizedList(new ArrayList<>());
List<Partition> allPartitions = Collections.synchronizedList(new ArrayList<>());
// OR use CopyOnWriteArrayList for read-heavy workloads
```

### 🔴 Critical Bug #4: Race Condition in Rebalance (ConsumerGroup.java)

```java
public void rebalance() {
    if (consumerToPartitions.isEmpty() || allPartitions.isEmpty()) {
        return;
    }
    List<String> consumers = new ArrayList<>(consumerToPartitions.keySet());
    consumerToPartitions = distributionStrategy.rebalance(consumers, allPartitions);
    // ⚠️ RACE: consumerToPartitions reference is being replaced while poll() might be reading
}
```

**Problem:** `consumerToPartitions` is reassigned during rebalance while `poll()` might be iterating over it.

**Fix:** Use synchronization or make the operation atomic:

```java
private final Object rebalanceLock = new Object();

public void rebalance() {
    synchronized (rebalanceLock) {
        if (consumerToPartitions.isEmpty() || allPartitions.isEmpty()) {
            return;
        }
        List<String> consumers = new ArrayList<>(consumerToPartitions.keySet());
        Map<String, List<Partition>> newAssignment = distributionStrategy.rebalance(consumers, allPartitions);
        consumerToPartitions.clear();
        consumerToPartitions.putAll(newAssignment);
    }
}
```

### 🔴 Critical Bug #5: Non-Thread-Safe Maps in Broker

```java
Map<String, Topic> topics;                    // HashMap - not thread-safe
Map<String, ConsumerGroup> consumerGroups;    // HashMap - not thread-safe
```

**Problem:** Multiple threads could call `createTopic()`, `addConsumerToConsumerGroup()`, or `publishToTopic()` concurrently.

**Fix:** Use `ConcurrentHashMap`:

```java
Map<String, Topic> topics = new ConcurrentHashMap<>();
Map<String, ConsumerGroup> consumerGroups = new ConcurrentHashMap<>();
```

### 🟡 Medium Bug #6: Non-Atomic Check-Then-Act in Broker

```java
public ConsumerGroup addConsumerToConsumerGroup(String groupId, String consumerId) {
    ConsumerGroup consumerGroup = consumerGroups.get(groupId);  // Check
    if (consumerGroup == null) {
        consumerGroup = new ConsumerGroup(groupId);             // Act
    }
    consumerGroups.put(groupId, consumerGroup);                 // Act
    // ⚠️ Two threads could create duplicate ConsumerGroups
```

**Fix:** Use `computeIfAbsent`:

```java
public ConsumerGroup addConsumerToConsumerGroup(String groupId, String consumerId) {
    ConsumerGroup consumerGroup = consumerGroups.computeIfAbsent(groupId,
        k -> new ConsumerGroup(k));
    consumerGroup.addConsumer(consumerId);
    return consumerGroup;
}
```

---

## 4. Functionality Issues

### 🔴 Bug #1: Incorrect Offset Handling in Topic.AddMessage()

```java
Optional<AtomicInteger> lastOffset = partition.getLastCommittedOffset();
int newOffset = lastOffset.isPresent() ? lastOffset.get().incrementAndGet() : 0;
```

**Problem:**

1. `getLastCommittedOffset()` creates a NEW `AtomicInteger` each time, so `incrementAndGet()` doesn't actually increment any persistent counter
2. First message gets offset 0, but the logic is convoluted

**Fix:** Partition should own and manage its offset counter internally.

### 🔴 Bug #2: Typo in Method Name

```java
public void susbcribe(String groupId, String topicName) // "susbcribe" instead of "subscribe"
```

### 🟡 Bug #3: Producer Class Has Unused Field

```java
public class Producer {
    IPartitionStrategy partitionStrategy; // Never used - Topic has its own strategy
```

**Fix:** Remove unused field or clarify the design intent.

### 🟡 Bug #4: Encapsulation Violation

```java
// In Topic.java
String name;                              // Should be private
Map<String, Partition> topicPartitions;   // Should be private

// In Partition.java
String id;                                // Should be private
Map<Integer, Message> messages;           // Should be private
```

**Fix:** Add `private` access modifier to all fields.

---

## 5. Code Quality Issues

### Naming Conventions

- `AddMessage` should be `addMessage` (Java convention: camelCase for methods)
- `susbcribe` is a typo for `subscribe`

### Exception Handling

- Using generic `Exception` instead of custom exceptions:
  ```java
  throw new Exception("Either Consumer Group or Topic does not exist");
  ```
  **Fix:** Create custom exceptions like `TopicNotFoundException`, `ConsumerGroupNotFoundException`

### Missing Validation

- No null checks on critical parameters
- No validation for negative partition counts

---

## 6. Summary of Required Fixes

| Priority    | Issue                                | Location           | Fix                                 |
| ----------- | ------------------------------------ | ------------------ | ----------------------------------- |
| 🔴 Critical | Race condition in offset calculation | Topic.java         | Move offset generation to Partition |
| 🔴 Critical | Unused ReadWriteLock                 | Partition.java     | Use it or remove it                 |
| 🔴 Critical | Non-thread-safe lists                | ConsumerGroup.java | Use synchronized lists              |
| 🔴 Critical | Race in rebalance                    | ConsumerGroup.java | Add synchronization                 |
| 🔴 Critical | Non-thread-safe maps                 | Broker.java        | Use ConcurrentHashMap               |
| 🔴 Critical | Broken offset logic                  | Topic.java         | Fix AtomicInteger usage             |
| 🟡 Medium   | Check-then-act race                  | Broker.java        | Use computeIfAbsent                 |
| 🟡 Medium   | DIP violation                        | ConsumerGroup.java | Inject IDistributionStrategy        |
| 🟡 Medium   | Missing encapsulation                | Multiple files     | Add private modifiers               |
| 🟡 Medium   | Typo in method name                  | Broker.java        | Rename to subscribe                 |
| 🟢 Low      | Unused field                         | Producer.java      | Remove partitionStrategy            |
| 🟢 Low      | Generic exceptions                   | Broker.java        | Create custom exceptions            |
| 🟢 Low      | Naming convention                    | Topic.java         | Rename AddMessage to addMessage     |

---

## 7. Overall Assessment

| Aspect           | Score      | Notes                                                     |
| ---------------- | ---------- | --------------------------------------------------------- |
| SOLID Principles | 6/10       | Good use of ISP and OCP, but DIP violations               |
| Design Patterns  | 7/10       | Strategy pattern well applied, but hardcoded dependencies |
| Concurrency      | 3/10       | Multiple critical race conditions and unused locks        |
| Code Quality     | 6/10       | Good structure but encapsulation and naming issues        |
| **Overall**      | **5.5/10** | Solid foundation but needs significant concurrency fixes  |

The design shows good understanding of Kafka concepts and proper use of the Strategy pattern. However, the concurrency handling has critical bugs that would cause data corruption in a multi-threaded environment. The offset management logic is fundamentally broken and needs to be redesigned to be atomic within the Partition class.
