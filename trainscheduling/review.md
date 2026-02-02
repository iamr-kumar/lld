# Train Scheduling System - Staff Engineer Code Review

**Review Date:** February 2025  
**Context:** SDE-2 Machine Coding Round (1 hour)  
**Reviewer Focus:** Design Principles, SOLID, Design Patterns, Concurrency, Code Quality

---

## Executive Summary

The solution demonstrates a **functional MVP** for the train scheduling problem with proper separation of concerns and use of appropriate data structures (PriorityQueue). However, there are **critical concurrency issues**, violations of SOLID principles, and missed opportunities for better design patterns that would be expected at SDE-2 level.

**Overall Rating: 6/10** (Functional but needs significant improvements for production readiness)

---

## 1. SOLID Principles Analysis

### ✅ Single Responsibility Principle (Partial)

- **Good:** Separation between `PlatformService` and `SchedulingService`
- **Issue:** `SchedulingService` handles too many responsibilities:
  - Platform state management (PriorityQueue)
  - Assignment storage
  - Scheduling logic
  - Time calculations
  - Query operations

**Recommendation:** Extract concerns:

```java
// Separate class for platform availability tracking
public class PlatformAvailabilityManager {
    private final PriorityQueue<PlatformState> platformQueue;
    // ...
}

// Separate class for assignment repository
public class AssignmentRepository {
    private final List<PlatformAssignment> assignments;
    // ...
}
```

### ❌ Open/Closed Principle (Violated)

- The scheduling strategy is **hardcoded** (earliest available platform)
- No way to extend with different scheduling strategies without modifying `SchedulingService`

**Recommendation:** Use Strategy Pattern:

```java
public interface SchedulingStrategy {
    PlatformState selectPlatform(PriorityQueue<PlatformState> availablePlatforms,
                                  ScheduleRequest request);
}

public class EarliestAvailableStrategy implements SchedulingStrategy { ... }
public class LeastUtilizedStrategy implements SchedulingStrategy { ... }
```

### ⚠️ Liskov Substitution Principle (N/A)

- No inheritance hierarchy to evaluate

### ❌ Interface Segregation Principle (Violated)

- No interfaces defined at all
- `PlatformService` and `SchedulingService` are concrete classes
- Makes testing and mocking difficult

**Recommendation:**

```java
public interface IPlatformService {
    List<Platform> getPlatforms();
    void addPlatform(int platformNumber);
}

public interface ISchedulingService {
    ScheduleResponse scheduleTrain(ScheduleRequest request);
    Optional<PlatformAssignment> getAssignmentForPlatformAtTime(String time, int platformNumber);
}
```

### ❌ Dependency Inversion Principle (Violated)

- `SchedulingService` depends on concrete `PlatformService`, not abstraction
- No dependency injection framework consideration

---

## 2. Concurrency Analysis (CRITICAL)

### 🚨 Critical Issue: Broken Thread Safety

The current `synchronized` on `scheduleTrain()` is **necessary but insufficient**:

```java
public synchronized ScheduleResponse scheduleTrain(ScheduleRequest request) {
    // ...
}
```

**Problems:**

#### 2.1 Non-Thread-Safe Read Operation

```java
public Optional<PlatformAssignment> getAssignmentForPlatformAtTime(String time, int platformNumber) {
    for (PlatformAssignment assignment : assignments) {  // ❌ Not synchronized!
        // ...
    }
}
```

- **Race condition:** A thread can read `assignments` while another thread is modifying it in `scheduleTrain()`
- **Result:** `ConcurrentModificationException` or inconsistent reads

#### 2.2 Non-Thread-Safe Data Structures

```java
PriorityQueue<PlatformState> platformQueue;      // ❌ Not thread-safe
List<PlatformAssignment> assignments;             // ❌ ArrayList not thread-safe
```

#### 2.3 Initialization Race Condition

```java
public void initializePlatformQueue(String startTime) {  // ❌ Not synchronized
    List<Platform> platforms = platformService.getPlatforms();
    for (Platform platform : platforms) {
        platformQueue.add(new PlatformState(platform, startTime));
    }
}
```

### ✅ Recommended Concurrency Fix (Production-Grade)

```java
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.CopyOnWriteArrayList;

public class SchedulingService {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final PriorityQueue<PlatformState> platformQueue;
    private final List<PlatformAssignment> assignments;

    public SchedulingService(PlatformService platformService) {
        this.platformQueue = new PriorityQueue<>(
            Comparator.comparing(PlatformState::getNextAvailableTime));
        this.assignments = new CopyOnWriteArrayList<>();  // Thread-safe for reads
        this.platformService = platformService;
    }

    public ScheduleResponse scheduleTrain(ScheduleRequest request) {
        lock.writeLock().lock();
        try {
            // ... scheduling logic
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<PlatformAssignment> getAssignmentForPlatformAtTime(String time, int platformNumber) {
        lock.readLock().lock();
        try {
            // ... query logic
        } finally {
            lock.readLock().unlock();
        }
    }
}
```

**Why ReadWriteLock?**

- Multiple threads can query simultaneously (read lock)
- Only one thread can schedule at a time (write lock)
- Better throughput than `synchronized` for read-heavy workloads

### Alternative: Lock-Free Design with ConcurrentHashMap

```java
public class LockFreePlatformManager {
    private final ConcurrentHashMap<Integer, AtomicReference<String>> platformNextAvailable;

    public boolean trySchedule(int platformNumber, String arrivalTime, String departureTime) {
        return platformNextAvailable.get(platformNumber)
            .compareAndSet(currentTime, departureTime);  // CAS operation
    }
}
```

---

## 3. Design Patterns Analysis

### ✅ Patterns Used

| Pattern    | Used?   | Quality                                   |
| ---------- | ------- | ----------------------------------------- |
| Builder    | ❌ No   | Could use for `ScheduleRequest`           |
| Strategy   | ❌ No   | Should use for scheduling algorithms      |
| Repository | Partial | `assignments` list acts as in-memory repo |
| Factory    | ❌ No   | Could use for creating responses          |
| Singleton  | ❌ No   | Not needed here                           |

### ⚠️ Missing Patterns

#### 3.1 Strategy Pattern (High Priority)

Current code has hardcoded "earliest available" logic. Should be:

```java
public interface PlatformSelectionStrategy {
    PlatformState select(PriorityQueue<PlatformState> queue, ScheduleRequest request);
}

public class EarliestAvailableStrategy implements PlatformSelectionStrategy {
    @Override
    public PlatformState select(PriorityQueue<PlatformState> queue, ScheduleRequest request) {
        return queue.poll();
    }
}
```

#### 3.2 Builder Pattern for Complex Objects

```java
public class ScheduleRequestBuilder {
    private Train train;
    private String arrivalTime;
    private int haltDuration;

    public ScheduleRequestBuilder withTrain(Train train) { ... }
    public ScheduleRequestBuilder arrivingAt(String time) { ... }
    public ScheduleRequestBuilder withHaltDuration(int minutes) { ... }
    public ScheduleRequest build() { ... }
}
```

#### 3.3 Repository Pattern (for better separation)

```java
public interface AssignmentRepository {
    void save(PlatformAssignment assignment);
    Optional<PlatformAssignment> findByPlatformAndTime(int platformNumber, String time);
    List<PlatformAssignment> findByPlatform(int platformNumber);
}

public class InMemoryAssignmentRepository implements AssignmentRepository {
    private final Map<Integer, TreeMap<String, PlatformAssignment>> platformAssignments;
    // O(log n) lookup instead of O(n)
}
```

---

## 4. Code Quality Issues

### 4.1 Time Representation (Major Issue)

```java
private final String nextAvailableTime;  // ❌ String for time
```

**Problems:**

- Lexicographic comparison works for "0000"-"2359" but breaks at midnight
- No timezone handling
- Error-prone arithmetic: `Integer.parseInt("1000")` = 1000, not 10:00

**Recommendation:**

```java
import java.time.LocalTime;
// OR
import java.time.Instant;

public class TimeWindow {
    private final LocalTime startTime;
    private final LocalTime endTime;

    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }
}
```

### 4.2 Missing Input Validation

```java
public ScheduleResponse scheduleTrain(ScheduleRequest request) {
    // ❌ No null checks
    // ❌ No validation of time format
    // ❌ No check for negative halt duration
    // ❌ No check if departure < arrival
}
```

**Recommendation:**

```java
public ScheduleResponse scheduleTrain(ScheduleRequest request) {
    Objects.requireNonNull(request, "Request cannot be null");
    Objects.requireNonNull(request.getTrain(), "Train cannot be null");

    if (request.getTimeWindow().getEndTime().compareTo(
            request.getTimeWindow().getStartTime()) < 0) {
        throw new IllegalArgumentException("Departure cannot be before arrival");
    }
    // ...
}
```

### 4.3 Query Performance Issue

```java
public Optional<PlatformAssignment> getAssignmentForPlatformAtTime(String time, int platformNumber) {
    for (PlatformAssignment assignment : assignments) {  // ❌ O(n) scan
        // ...
    }
}
```

**Should be O(log n) with proper indexing:**

```java
// Use TreeMap per platform for efficient range queries
Map<Integer, TreeMap<String, PlatformAssignment>> platformSchedules;

public Optional<PlatformAssignment> getAssignmentForPlatformAtTime(String time, int platformNumber) {
    TreeMap<String, PlatformAssignment> schedule = platformSchedules.get(platformNumber);
    if (schedule == null) return Optional.empty();

    Map.Entry<String, PlatformAssignment> entry = schedule.floorEntry(time);
    if (entry != null && entry.getValue().getTimeWindow().getEndTime().compareTo(time) >= 0) {
        return Optional.of(entry.getValue());
    }
    return Optional.empty();
}
```

### 4.4 Field Visibility

```java
public class SchedulingService {
    PriorityQueue<PlatformState> platformQueue;  // ❌ Package-private (no modifier)
    List<PlatformAssignment> assignments;         // ❌ Package-private
    PlatformService platformService;              // ❌ Package-private
}
```

**Should be:**

```java
private final PriorityQueue<PlatformState> platformQueue;
private final List<PlatformAssignment> assignments;
private final PlatformService platformService;
```

### 4.5 Missing Edge Case Handling

Per requirements, these are not handled:

- Multiple trains arriving at same time (FIFO fairness not guaranteed)
- Zero halt duration trains
- Invalid platform queries (returns empty, no explicit error)

---

## 5. API Design Issues

### 5.1 Inconsistent Method Naming

```java
scheduleTrain(ScheduleRequest request)           // ✅ Good
getAssignmentForPlatformAtTime(String time, int platformNumber)  // ⚠️ Parameter order inconsistent
```

**Should be:**

```java
getAssignmentForPlatformAtTime(int platformNumber, String time)  // Platform first, then time
// OR better naming:
queryPlatformSchedule(int platformNumber, String time)
```

### 5.2 Initialization Smell

```java
schedulingService.initializePlatformQueue("0000");  // ❌ Must call manually
```

**Should initialize in constructor or use lazy initialization**

---

## 6. What's Good ✅

1. **Immutable Models:** `Train`, `Platform`, `TimeWindow`, `PlatformAssignment` are immutable
2. **Data Structure Choice:** `PriorityQueue` for O(log n) platform selection is correct
3. **Separation of Concerns:** Service layer separate from models
4. **Clear Naming:** Class and method names are descriptive
5. **Use of Optional:** Proper use of `Optional` for nullable returns
6. **Enum for Status:** Using enum instead of strings/magic numbers

---

## 7. Recommended Refactored Architecture

```
├── models/
│   ├── Train.java
│   ├── Platform.java
│   ├── TimeWindow.java (use LocalTime)
│   ├── PlatformAssignment.java
│   └── PlatformState.java
├── dto/
│   ├── ScheduleRequest.java
│   └── ScheduleResponse.java
├── repository/
│   ├── IAssignmentRepository.java
│   └── InMemoryAssignmentRepository.java
├── strategy/
│   ├── PlatformSelectionStrategy.java
│   └── EarliestAvailableStrategy.java
├── service/
│   ├── IPlatformService.java
│   ├── PlatformService.java
│   ├── ISchedulingService.java
│   └── SchedulingService.java
├── manager/
│   └── PlatformAvailabilityManager.java
├── exception/
│   ├── PlatformNotFoundException.java
│   └── InvalidScheduleRequestException.java
└── TrainScheduler.java
```

---

## 8. Concurrency Deep Dive: Better Approaches

### Option A: Fine-Grained Locking (Recommended for Interview)

```java
public class ConcurrentSchedulingService implements ISchedulingService {
    private final ReadWriteLock scheduleLock = new ReentrantReadWriteLock();
    private final ConcurrentHashMap<Integer, ReentrantLock> platformLocks;

    public ScheduleResponse scheduleTrain(ScheduleRequest request) {
        scheduleLock.writeLock().lock();
        try {
            PlatformState selected = platformQueue.poll();
            ReentrantLock platformLock = platformLocks.get(selected.getPlatform().getPlatformNumber());
            platformLock.lock();
            try {
                // Atomic assignment to this platform
            } finally {
                platformLock.unlock();
            }
        } finally {
            scheduleLock.writeLock().unlock();
        }
    }
}
```

### Option B: Actor Model / Event Sourcing

```java
// Each platform is an actor processing messages sequentially
public class PlatformActor {
    private final BlockingQueue<ScheduleCommand> commandQueue;

    public CompletableFuture<ScheduleResponse> schedule(ScheduleRequest request) {
        CompletableFuture<ScheduleResponse> future = new CompletableFuture<>();
        commandQueue.put(new ScheduleCommand(request, future));
        return future;
    }
}
```

### Option C: Optimistic Locking with Versioning

```java
public class PlatformState {
    private final Platform platform;
    private final String nextAvailableTime;
    private final long version;  // Increment on each update

    public PlatformState withNewAvailableTime(String time) {
        return new PlatformState(platform, time, version + 1);
    }
}
```

---

## 9. Time Complexity Analysis

| Operation              | Current  | Optimal               |
| ---------------------- | -------- | --------------------- |
| Schedule Train         | O(log p) | O(log p) ✅           |
| Query Platform at Time | O(n)     | O(log n) with TreeMap |
| Initialize             | O(p)     | O(p) ✅               |

Where: p = number of platforms, n = number of assignments

---

## 10. Interview-Specific Feedback

**For a 1-hour machine coding round:**

### What Would Pass ✅

- Working solution
- Correct use of PriorityQueue
- Basic separation of concerns
- Immutable models

### What Would Stand Out 🌟

- Thread-safe implementation with ReadWriteLock
- Strategy pattern for platform selection
- Proper time handling with LocalTime
- O(log n) query with TreeMap indexing
- Comprehensive input validation

### What Would Raise Concerns ⚠️

- Broken thread safety (critical bug)
- O(n) query performance
- No input validation
- String-based time handling
- No interfaces for testability

---

## Summary of Action Items

| Priority    | Issue                       | Fix                                      |
| ----------- | --------------------------- | ---------------------------------------- |
| 🔴 Critical | Thread safety on reads      | Add synchronization or use ReadWriteLock |
| 🔴 Critical | Non-thread-safe collections | Use concurrent collections               |
| 🟡 High     | No interfaces               | Add interfaces for services              |
| 🟡 High     | O(n) query                  | Use TreeMap for indexing                 |
| 🟡 High     | String time handling        | Use LocalTime/Instant                    |
| 🟢 Medium   | No input validation         | Add validation                           |
| 🟢 Medium   | No strategy pattern         | Extract scheduling strategy              |
| 🟢 Low      | Field visibility            | Make fields private final                |
