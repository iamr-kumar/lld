# Train Platform Scheduling System

## Problem Statement

Design a **Train Platform Scheduling System** for a railway station with a fixed number of platforms. Trains arrive at specific times and require a certain halt duration. The system should efficiently assign platforms and handle scheduling conflicts.

### Functional Requirements

1. **Schedule an Incoming Train** — Assign platform and actual start time; a platform can host only one train at a time
2. **Handle Waiting** — If all platforms are busy, schedule on earliest available platform while preserving halt duration
3. **Query Platform Schedule** — Return which train (if any) occupies a platform at a given time

### Non-Functional Requirements

- Optimized for frequent scheduling and fast availability lookup
- Handle edge cases: simultaneous arrivals, zero halt duration, invalid queries
- Thread-safe for concurrent operations

---

## Architecture Overview

### File Structure

```
src/
├── TrainScheduler.java          # Entry point with comprehensive test suite
├── enums/
│   └── Status.java              # SCHEDULED, WAITING states
├── models/
│   ├── Train.java               # Immutable train entity
│   ├── Platform.java            # Immutable platform entity
│   ├── TimeWindow.java          # Start/end time encapsulation (LocalTime)
│   ├── PlatformState.java       # Platform + next available time
│   ├── PlatformAssignment.java  # Train-Platform-TimeWindow binding
│   ├── ScheduleRequest.java     # Input DTO
│   └── ScheduleResponse.java    # Output DTO with status
├── services/
│   ├── ISchedulingService.java  # Core scheduling interface
│   ├── SchedulingService.java   # Main orchestrator with ReadWriteLock
│   ├── IPlatformService.java    # Platform management interface
│   └── PlatformService.java     # Platform CRUD operations
├── manager/
│   ├── IPlatformAvailabilityManager.java
│   └── PlatformAvailabilityManager.java  # PriorityQueue-based availability
└── repository/
    ├── IAssignmentRepository.java
    └── AssignmentRepository.java  # In-memory assignment storage
```

---

## Design Highlights

### Strong Points

**1. Clean Separation of Concerns**

- `SchedulingService` orchestrates the flow without managing data structures directly
- `PlatformAvailabilityManager` owns the PriorityQueue logic
- `AssignmentRepository` handles persistence independently

**2. Interface-Driven Design**

- All major components have interfaces (`ISchedulingService`, `IPlatformService`, `IAssignmentRepository`, `IPlatformAvailabilityManager`)
- Enables easy mocking for unit tests and future implementations (e.g., database-backed repository)

**3. Immutable Models**

- `Train`, `Platform`, `TimeWindow`, `PlatformAssignment`, `PlatformState` are all immutable
- Eliminates entire classes of concurrency bugs

**4. Proper Time Handling**

- Uses `java.time.LocalTime` instead of string-based time
- `Duration` for calculating halt periods
- Handles time arithmetic correctly

**5. Thread Safety with ReadWriteLock**

- `SchedulingService` uses `ReentrantReadWriteLock` for concurrent access
- Write lock for scheduling (exclusive), read lock for queries (shared)
- Allows multiple concurrent reads while ensuring write exclusivity

**6. Optimal Data Structure Choice**

- `PriorityQueue` for O(log p) platform selection by earliest availability
- `HashMap` with platform-keyed lists for O(1) + O(n) queries

**7. Comprehensive Test Coverage**

- 27 test cases covering basic, edge, and concurrent scenarios
- Stress tests with 100 trains and 20 threads
- Uses `CountDownLatch` for synchronized concurrent testing

---

## SOLID Principles Adherence

| Principle                 | Status     | Implementation                                                                                          |
| ------------------------- | ---------- | ------------------------------------------------------------------------------------------------------- |
| **Single Responsibility** | ✅         | Each class has one reason to change — scheduling logic, availability tracking, and storage are separate |
| **Open/Closed**           | ⚠️ Partial | Interfaces allow extension, but scheduling strategy is not pluggable                                    |
| **Liskov Substitution**   | ✅         | Implementations are interchangeable via interfaces                                                      |
| **Interface Segregation** | ✅         | Small, focused interfaces (e.g., `IAssignmentRepository` has only 2 methods)                            |
| **Dependency Inversion**  | ✅         | `SchedulingService` depends on abstractions, not concrete classes                                       |

---

## Time Complexity

| Operation              | Complexity | Notes                                 |
| ---------------------- | ---------- | ------------------------------------- |
| Schedule Train         | O(log p)   | PriorityQueue poll + offer            |
| Query Platform at Time | O(n)       | Linear scan of platform's assignments |
| Add Platform           | O(1)       | ArrayList append                      |

Where: p = number of platforms, n = assignments for queried platform

---

## Potential Improvements

### Concurrency Enhancements

1. **Fine-Grained Locking** — Current write lock blocks entire scheduling; could use per-platform locks for parallel scheduling to different platforms

2. **Lock-Free Data Structures** — Replace PriorityQueue with ConcurrentSkipListMap for lock-free platform selection

3. **Fairness Guarantee** — Use ReentrantReadWriteLock(true) for fair ordering of waiting threads

### Performance Optimizations

4. **O(log n) Query Lookup** — Replace ArrayList per platform with TreeMap for binary search on time ranges

5. **Indexing by Time** — Add secondary index for faster cross-platform time-based queries

### Design Pattern Additions

6. **Strategy Pattern** — Extract platform selection logic into PlatformSelectionStrategy interface (e.g., EarliestAvailableStrategy, LeastUtilizedStrategy, RoundRobinStrategy)

7. **Builder Pattern** — Add ScheduleRequest.Builder for cleaner request construction with optional parameters

8. **Observer Pattern** — Notify listeners on scheduling events (useful for logging, metrics, or UI updates)

### Robustness

9. **Input Validation** — Add null checks, time range validation, duplicate train detection

10. **Custom Exceptions** — Create PlatformNotFoundException, InvalidTimeRangeException for better error handling

11. **Cancellation Support** — Add ability to cancel/reschedule trains and free up platform slots

### Scalability

12. **Event Sourcing** — Store scheduling events for audit trail and replay capability

13. **Distributed Support** — Abstract repository for Redis/database backing in multi-instance deployments

---

## How to Run

```bash
cd src
javac -d ../out $(find . -name "*.java")
java -cp ../out TrainScheduler
```

Runs the full test suite with 27 tests including concurrent stress tests.
