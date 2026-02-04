# Meeting Room Scheduler

## Problem Statement

Design a **Meeting Room Scheduler** for an organization with multiple meeting rooms. Users can schedule meetings specifying time windows, attendee count, and room preferences. The system should automatically assign suitable rooms and handle concurrent booking requests safely.

### Functional Requirements

1. **Schedule a Meeting** — Create a meeting with title, time window, attendees, and room type preference
2. **Auto-assign Room** — Automatically find and assign an available room matching capacity and type requirements
3. **Cancel Meeting** — Remove a scheduled meeting and free up the room slot
4. **Query User Meetings** — Retrieve all meetings for a user (as host or attendee)
5. **Handle Room Conflicts** — Reject bookings when no suitable room is available for the requested time

### Non-Functional Requirements

- Thread-safe for concurrent booking/cancellation requests
- Fine-grained locking to maximize parallel throughput
- Efficient room availability lookup
- Handle edge cases: overlapping times, invalid inputs, double cancellation

---

## Architecture Overview

### File Structure

```
src/
├── enums/
│   ├── MeetingStatus.java       # SCHEDULED, CANCELED, COMPLETED states
│   └── RoomType.java            # CONFERENCE_ROOM, PROJECT_ROOM types
├── models/
│   ├── Meeting.java             # Meeting entity with host, attendees, room
│   ├── Room.java                # Immutable room entity (floor, number, type, capacity)
│   ├── RoomBookings.java        # TreeSet-based booking tracker per room
│   ├── TimeWindow.java          # Start/end time encapsulation (LocalDateTime)
│   └── User.java                # User entity (name, email)
├── repository/
│   ├── IMeetingRepository.java  # Meeting persistence interface
│   ├── MeetingRepository.java   # TreeMap-based meeting storage with RWLock
│   ├── IRoomRepository.java     # Room management interface
│   └── RoomRepository.java      # ConcurrentHashMap-based room storage
└── services/
    ├── IMeetingService.java     # Core scheduling interface
    ├── MeetingService.java      # Main orchestrator with fine-grained room locks
    ├── IRoomService.java        # Room operations interface
    └── RoomService.java         # Room availability and booking management
```

---

## Design Highlights

### Strong Points

**1. Fine-Grained Locking Strategy**

- `MeetingService` uses per-room `ReentrantReadWriteLock` instead of a global lock
- Allows parallel booking on different rooms while serializing access to the same room
- Stored in `ConcurrentHashMap<Room, ReentrantReadWriteLock>` for thread-safe lock management

**2. Clean Separation of Concerns**

- `MeetingService` orchestrates scheduling flow and owns concurrency control
- `RoomService` handles room availability queries and booking operations
- `MeetingRepository` manages meeting persistence independently
- `RoomRepository` + `RoomBookings` handle room-level booking data

**3. Interface-Driven Design**

- All major components have interfaces (`IMeetingService`, `IRoomService`, `IMeetingRepository`, `IRoomRepository`)
- Enables easy mocking for unit tests and future implementations (e.g., database-backed storage)

**4. Optimal Data Structure Choices**

- `TreeSet<TimeWindow>` in `RoomBookings` for O(log n) overlap detection using floor/ceiling operations
- `TreeMap<LocalDateTime, TreeSet<Meeting>>` in `MeetingRepository` for time-ordered meeting storage
- `ConcurrentHashMap` for thread-safe room storage

**5. Double-Check Locking Pattern**

- First check: `getAvailableRoomForRequest()` without lock (fast path)
- Second check: `isRoomAvailable()` with lock held (ensures consistency)
- Prevents race conditions where room becomes unavailable between check and book

**6. Proper Time Handling**

- Uses `java.time.LocalDateTime` for precise scheduling
- `Duration` for calculating meeting lengths
- `TimeWindow.overlapsWith()` for conflict detection

**7. Thread Safety in Repositories**

- `MeetingRepository` uses `ReentrantReadWriteLock` for read/write separation
- Multiple concurrent reads allowed, writes are exclusive
- `RoomRepository` uses `ConcurrentHashMap` for atomic operations

---

## SOLID Principles Adherence

| Principle                 | Status     | Implementation                                                                                    |
| ------------------------- | ---------- | ------------------------------------------------------------------------------------------------- |
| **Single Responsibility** | ✅         | Each class has one reason to change — scheduling, room management, and persistence are separate   |
| **Open/Closed**           | ⚠️ Partial | Interfaces allow extension, but room selection strategy is not pluggable                          |
| **Liskov Substitution**   | ✅         | Implementations are interchangeable via interfaces                                                |
| **Interface Segregation** | ✅         | Small, focused interfaces (e.g., `IRoomRepository` has only 5 methods)                            |
| **Dependency Inversion**  | ✅         | `MeetingService` depends on `IRoomService` and `IMeetingRepository` abstractions, not concretions |

---

## Time Complexity

| Operation               | Complexity | Notes                                            |
| ----------------------- | ---------- | ------------------------------------------------ |
| Schedule Meeting        | O(r log b) | r rooms checked, log b for TreeSet overlap check |
| Cancel Meeting          | O(log m)   | TreeSet removal in repository                    |
| Check Room Availability | O(log b)   | TreeSet floor/ceiling operations                 |
| Get Meetings for User   | O(m)       | Linear scan of all meetings                      |
| Get Available Rooms     | O(r log b) | Iterate rooms, check availability for each       |

Where: r = number of rooms, b = bookings per room, m = total meetings

---

## Concurrency Model

```
┌─────────────────────────────────────────────────────────────────┐
│                     MeetingService                               │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  Map<Room, ReentrantReadWriteLock> roomLocks            │    │
│  │                                                          │    │
│  │  Room A Lock ──┬── Thread 1: Schedule meeting (Room A)  │    │
│  │                └── Thread 2: Cancel meeting (Room A)     │    │
│  │                     [BLOCKED - waits for Thread 1]       │    │
│  │                                                          │    │
│  │  Room B Lock ──── Thread 3: Schedule meeting (Room B)   │    │
│  │                     [PARALLEL - different lock]          │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

---

## Potential Improvements

### Concurrency Enhancements

1. **Read Lock for Availability Check** — Currently holding write lock during entire scheduling; could use read lock for initial availability check, upgrade to write only for booking

2. **Lock-Free RoomBookings** — Replace `TreeSet` with `ConcurrentSkipListSet` for lock-free operations within room bookings

3. **Fairness Guarantee** — Use `ReentrantReadWriteLock(true)` for fair ordering of waiting threads

### Design Pattern Additions

4. **Strategy Pattern** — Extract room selection logic into `RoomSelectionStrategy` interface (e.g., `FirstAvailableStrategy`, `SmallestFitStrategy`, `LeastUsedStrategy`)

5. **Builder Pattern** — Add `Meeting.Builder` for cleaner meeting construction

6. **Observer Pattern** — Notify listeners on meeting events (useful for calendar sync, notifications)

### Performance Optimizations

7. **Indexed User Lookups** — Add secondary index `Map<User, Set<Meeting>>` for O(1) user meeting queries instead of O(m) scan

8. **Capacity-Based Room Ordering** — Sort available rooms by capacity to prefer smallest suitable room

### Robustness

9. **Input Validation** — Add comprehensive null checks, time range validation, capacity validation

10. **Custom Exceptions** — Create `RoomUnavailableException`, `InvalidTimeWindowException` for better error handling

11. **Meeting Update Support** — Add ability to reschedule meetings (change time/room)

### Scalability

12. **Event Sourcing** — Store booking events for audit trail and replay capability

13. **Distributed Locking** — Abstract locking mechanism for Redis/ZooKeeper in multi-instance deployments

---

## How to Run

```bash
cd /path/to/lld
javac meetingscheduler/MeetingSchedulerDriver.java meetingscheduler/src/**/*.java
java meetingscheduler.MeetingSchedulerDriver
```

Runs the test suite with 18 tests including concurrent stress tests.
