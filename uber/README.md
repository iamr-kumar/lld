# UBER (Simplified Ride Matching)

## Problem Statement

Design and implement a **simplified Uber-like ride matching system** that supports:

1. **Ride requests** — Riders can request a ride with pickup and dropoff locations
2. **Driver matching** — Assign a driver based on a matching strategy (e.g., nearest driver)
3. **Driver notification + response** — Send a request to a driver and wait for accept/reject with timeout
4. **Retries** — If a driver rejects (or is unavailable), retry with another driver (exclude previously attempted drivers)
5. **Sync and async requests** — Support blocking (sync) and non-blocking (async) request flows
6. **Concurrency** — Process multiple ride requests concurrently using workers

### Non-Functional Requirements (Current Scope)

- Thread-safe, multi-request simulation
- Simple extensibility for new matching strategies
- Clear separation of concerns (controllers are not implemented; this is a driver/demo app)

---

## Architecture Overview

### File Structure

```text
src/
├── UberApplication.java              # Demo/driver: spins up workers and runs scenarios
├── enums/
│   ├── DriverResponse.java           # ACCEPT / REJECT
│   ├── DriverStatus.java             # Driver lifecycle states
│   └── RideStatus.java               # Ride lifecycle states
├── models/
│   ├── Driver.java                   # Driver + concurrency lock
│   ├── Location.java                 # Coordinates + distance
│   ├── Ride.java                     # Ride aggregate + excluded drivers
│   ├── Rider.java                    # Rider
│   ├── RideRequest.java              # Ride + CompletableFuture result
│   └── base/
│       └── User.java                 # Base user model
├── repositories/
│   ├── IDriverRepository.java        # Driver repository contract
│   ├── DriverRepository.java         # In-memory driver store
│   ├── IRideRepository.java          # Ride repository contract
│   └── RideRepository.java           # In-memory ride store
├── services/
│   ├── IRideService.java             # Public ride request API
│   ├── RideService.java              # Enqueue requests + complete rides
│   ├── IRideMatchingService.java     # Driver assignment contract
│   ├── RideMatchingService.java      # Driver matching + retries + notifications
│   ├── IDriverNotificationService.java
│   └── DriverNotificationService.java # Simulated driver response + timeouts
├── strategies/
│   ├── IDriverMatchingStrategy.java  # Strategy contract
│   └── NearestDriverStrategy.java    # Concrete strategy
└── worker/
    └── RideMatchingWorker.java       # Worker that consumes queue and assigns drivers
```

---

## Current Design (How It Works)

### High-Level Flow

1. `UberApplication` initializes repositories and services.
2. A bounded `BlockingQueue<RideRequest>` acts as an in-memory request queue.
3. Multiple `RideMatchingWorker` threads poll the queue and attempt assignment.
4. `RideService` creates a `Ride`, persists it, then enqueues a `RideRequest`.
5. Each `RideRequest` contains a `CompletableFuture<Driver>` that is completed by the worker:
   - success → completes with assigned driver
   - failure → completes with `null`
6. `RideMatchingService` performs retries:
   - fetches available drivers from repository
   - uses `IDriverMatchingStrategy` to pick a candidate (currently nearest)
   - attempts to lock the driver (`Driver.tryLockForRide()`)
   - notifies driver and waits for accept/reject (simulated)
   - on reject, excludes the driver for that ride and retries

### Concurrency Model

- **Queue level:** `LinkedBlockingQueue` buffers ride requests.
- **Worker level:** a fixed thread pool runs multiple `RideMatchingWorker` instances.
- **Driver level:** `Driver` uses a `ReentrantLock` to prevent two workers from assigning the same driver concurrently.
- **Notification level:** `DriverNotificationService` uses its own executor and `Future.get(timeout)` to simulate response timeouts.

---

## Design Highlights

### Strong Points

#### 1) Strategy Pattern (Driver Matching)

- `IDriverMatchingStrategy` defines the selection contract.
- `NearestDriverStrategy` picks the nearest driver based on `Location.distanceTo(...)`.
- Adding new strategies (e.g., rating-based, surge-aware, zone-based) is straightforward.

#### 2) Producer–Consumer with Workers

- `RideService` produces `RideRequest`s into a queue.
- `RideMatchingWorker` consumes requests and completes the corresponding future.
- Enables asynchronous behavior without requiring external infra.

#### 3) Retry + Exclusion Mechanism

- `Ride` keeps a set of excluded driver IDs.
- After a rejection (or failed lock), the driver can be excluded and another candidate can be tried.

#### 4) Repository Abstraction

- `IDriverRepository` / `IRideRepository` hide storage details.
- Current implementation is in-memory (`ConcurrentHashMap`-based) and thread-safe.

#### 5) Clear Service Boundaries

- `RideService` = ride lifecycle orchestration + queue interaction.
- `RideMatchingService` = assignment logic.
- `DriverNotificationService` = notification/timeout simulation.

---

## SOLID Principles Adherence

| Principle                       | Status | Notes                                                                                                              |
| ------------------------------- | ------ | ------------------------------------------------------------------------------------------------------------------ |
| **Single Responsibility (SRP)** | ✅     | Models represent state; repositories store; services orchestrate; strategy selects drivers; worker consumes queue. |
| **Open/Closed (OCP)**           | ✅     | Add new `IDriverMatchingStrategy` implementations without changing core request APIs.                              |
| **Liskov Substitution (LSP)**   | ✅     | Services depend on interfaces (`IRideService`, `IRideMatchingService`, repositories) allowing safe substitution.   |
| **Interface Segregation (ISP)** | ✅     | Small focused interfaces (`IDriverRepository`, `IRideRepository`, `IDriverNotificationService`, etc.).             |
| **Dependency Inversion (DIP)**  | ⚠️     | Interfaces exist, but `UberApplication` wires concrete implementations directly (no DI container/factory).         |

---

## Design Patterns Used

| Pattern                      | Usage                                                                      |
| ---------------------------- | -------------------------------------------------------------------------- |
| **Strategy**                 | Driver selection (`IDriverMatchingStrategy` → `NearestDriverStrategy`)     |
| **Producer–Consumer**        | `RideService` enqueues, workers dequeue                                    |
| **Command-ish via Runnable** | `RideMatchingWorker` encapsulates work loop                                |
| **Repository**               | In-memory persistence abstraction (`IDriverRepository`, `IRideRepository`) |

---

## Time Complexity (Approx.)

Let:

- $D$ = number of drivers
- $A$ = number of available drivers
- $K$ = max attempts per ride (currently `MAX_DRIVER_ATTEMPTS`)

| Operation                               | Complexity    | Notes                                             |
| --------------------------------------- | ------------- | ------------------------------------------------- |
| `getAvailableDrivers()`                 | $O(D)$        | Filters all drivers in memory (stream filter).    |
| `NearestDriverStrategy.findDriver(...)` | $O(A)$        | Scans available drivers to find minimum distance. |
| One assignment attempt                  | $O(D + A)$    | Available-driver fetch + nearest selection.       |
| Worst-case matching per ride            | $O(K(D + A))$ | Up to `MAX_DRIVER_ATTEMPTS` retries.              |
| Enqueue ride request                    | $O(1)$        | `BlockingQueue.offer(...)`.                       |
| Sync request wait                       | blocking      | Waits on `CompletableFuture.get(timeout)`.        |

---

## Demo Scenarios Included

`UberApplication` demonstrates:

- **Synchronous ride**: request blocks until assigned or timeout
- **Asynchronous ride**: request returns immediately; caller uses the future
- **Concurrent rides**: multiple riders submit requests concurrently
- **High contention**: more riders than available drivers

---

## How to Run

From repo root:

```bash
javac uber/src/**/*.java uber/src/UberApplication.java
java uber.src.UberApplication
```

---

## Future Improvements

### Product / Feature Enhancements

1. **Pricing + payments** (fare calculation, split payments)
1. **Cancellations** (rider cancel, driver cancel, time-based penalties)
1. **Driver ETA + routing** (use real geo distance, routes, traffic)
1. **Driver/rider ratings** and filtering logic
1. **Multiple vehicle types** (bike, auto, sedan) + constraints

### Engineering / Design Enhancements

1. **Proper DI**: inject repositories/services/strategies instead of instantiating in `UberApplication`
1. **Event-driven workflow**: replace in-memory queue with Kafka/RabbitMQ for scalability
1. **State machine**: formalize ride/driver state transitions and validate invariants
1. **Metrics and tracing**: request latency, retries, assignment success ratio
1. **Backpressure strategy**: explicit handling when request queue is full (reject, shed load, etc.)

### Concurrency / Correctness

1. **Fairness**: avoid starvation (same drivers repeatedly picked)
1. **Idempotency**: ensure a ride request cannot be completed twice
1. **Stronger locking / reservation**: reserve driver with lease + expiry rather than immediate lock/release

### Code Quality / Cleanup

1. **Fix typos and naming**: `DriverStatus.TIMOUT` → `TIMEOUT` and align statuses with usage
1. **Custom exceptions**: replace `System.out.println` error paths with typed exceptions where appropriate
1. **Tests**: unit tests for strategies and matching; concurrency tests for double-assignment prevention
