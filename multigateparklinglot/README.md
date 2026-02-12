# Multi-Gate Parking Lot System

## Problem Statement

Design a **Multi-Gate Parking Lot System** that supports parking and unparking vehicles across multiple floors, with multiple entry and exit gates operating concurrently. The system should handle simultaneous park/unpark requests from different gates without race conditions, support multiple vehicle types, and calculate fees using configurable pricing strategies.

### Functional Requirements

1. **Park Vehicle** — Assign an available parking spot matching the vehicle type, issue a ticket
2. **Unpark Vehicle** — Process payment based on duration and fee strategy, free the spot
3. **Multiple Vehicle Types** — Support cars, bikes, heavy vehicles, and others with type-specific spots
4. **Fee Calculation** — Calculate parking fee based on configurable strategy (hourly, daily)
5. **Payment Processing** — Support multiple payment methods (Cash, UPI)
6. **Multi-Floor Layout** — Organize parking spots across multiple floors

### Non-Functional Requirements

- Thread-safe for concurrent park/unpark requests from multiple gates
- Per-spot locking with retry mechanism to maximize parallel throughput
- Graceful handling of full-lot scenarios and invalid ticket IDs

---

## Architecture Overview

### File Structure

```
src/
├── engine/
│   └── ParkingEngine.java            # Orchestrator — coordinates parking flow with locking
├── enums/
│   ├── FeeType.java                  # HOURLY, DAILY
│   ├── GateType.java                 # ENTRY, EXIT
│   ├── ParkingLotState.java          # AVAILABLE, OCCUPIED, RESERVED, OUT_OF_SERVICE
│   ├── PaymentType.java              # CASH, UPI
│   └── VehicleType.java              # BIKE, CAR, HEAVY_VEHICLE, OTHERS
├── factory/
│   ├── FeeFactory.java               # Creates FeeCalculationStrategy from FeeType
│   ├── PaymentFactory.java           # Creates PaymentStrategy from PaymentType
│   └── VehicleFactory.java           # Creates Vehicle subclass from VehicleType
├── models/
│   ├── gate/
│   │   ├── Gate.java                 # Abstract gate with ID and type
│   │   ├── EntryGate.java            # Entry gate
│   │   └── ExitGate.java             # Exit gate
│   ├── parking/
│   │   ├── ParkingFloor.java         # Floor containing multiple ParkingSpots
│   │   └── ParkingSpot.java          # Single spot with AtomicReference state
│   ├── ticket/
│   │   └── Ticket.java               # Ticket with vehicle, spot, entry/exit time, fee strategy
│   └── vehicle/
│       ├── Vehicle.java              # Abstract vehicle with plate and type
│       ├── Car.java                  # Car subclass
│       ├── Bike.java                 # Bike subclass
│       └── Others.java               # Others subclass
├── repository/
│   └── ParkingRepository.java        # ConcurrentHashMap-based floor/spot/gate storage
├── services/
│   ├── ParkingService.java           # Floor/spot management and availability lookup
│   ├── TicketService.java            # Ticket lifecycle — create, get, remove
│   └── PaymentService.java           # Fee calculation and payment processing
└── strategy/
    ├── fee/
    │   ├── FeeCalculationStrategy.java   # Strategy interface for fee calculation
    │   └── HourlyFeeCalculation.java     # Minimum fee + hourly rate
    └── payment/
        ├── PaymentStrategy.java          # Strategy interface for payment
        ├── CashPaymentStrategy.java      # Cash payment
        └── UPIPaymentStrategy.java       # UPI payment
```

---

## Design Highlights

### 1. Layered Architecture

Clear separation into Engine → Services → Repository:

- **`ParkingEngine`** — Orchestrates the park/unpark flow, owns concurrency control (locking + retry)
- **`ParkingService`** — Manages floors, spots, and availability queries
- **`TicketService`** — Manages ticket lifecycle (create, lookup, remove) using `ConcurrentHashMap`
- **`PaymentService`** — Handles fee calculation and payment delegation
- **`ParkingRepository`** — Owns the storage of floors, spots, and gates

### 2. Strategy Pattern

Two independent strategy axes, both resolved via factory:

- **`FeeCalculationStrategy`** — Pluggable fee models (`HourlyFeeCalculation`). Add daily, flat-rate, etc. without modifying existing code
- **`PaymentStrategy`** — Pluggable payment methods (`CashPaymentStrategy`, `UPIPaymentStrategy`). Add card, wallet, etc. freely

### 3. Factory Pattern

Three factories encapsulate object creation:

- **`VehicleFactory`** — Creates the right `Vehicle` subclass from `VehicleType`
- **`FeeFactory`** — Resolves `FeeType` → `FeeCalculationStrategy`
- **`PaymentFactory`** — Resolves `PaymentType` → `PaymentStrategy`

### 4. Per-Spot Locking with Retry

`ParkingEngine` uses a `ConcurrentHashMap<String, ReentrantLock>` keyed by spot ID. When a spot is found available, the engine locks only that spot, re-checks availability (double-check pattern), and retries up to 3 times if the spot was taken between find and lock.

### 5. Atomic State Transitions

`ParkingSpot` uses `AtomicReference<ParkingLotState>` with `compareAndSet` for state transitions (`AVAILABLE → OCCUPIED`, `OCCUPIED → AVAILABLE`), providing an additional safety net against invalid state changes.

---

## SOLID Principles Adherence

| Principle                 | Status     | Implementation                                                                                                                                                   |
| ------------------------- | ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Single Responsibility** | ✅         | `ParkingEngine` orchestrates, `ParkingService` manages spots, `TicketService` manages tickets, `PaymentService` handles payments — each has one reason to change |
| **Open/Closed**           | ✅         | New fee models and payment methods are added by implementing `FeeCalculationStrategy` / `PaymentStrategy` — no modification to existing classes needed           |
| **Liskov Substitution**   | ✅         | `Vehicle` subclasses (`Car`, `Bike`, `Others`) and strategy implementations are interchangeable through their base types                                         |
| **Interface Segregation** | ⚠️ Partial | Strategy interfaces are small and focused; however, services use concrete classes rather than interfaces                                                         |
| **Dependency Inversion**  | ⚠️ Partial | `ParkingEngine` depends on concrete `ParkingService`, `TicketService`, `PaymentService` — could depend on abstractions instead                                   |

---

## Design Patterns Used

| Pattern      | Where Used                                       | Purpose                                                           |
| ------------ | ------------------------------------------------ | ----------------------------------------------------------------- |
| **Strategy** | `FeeCalculationStrategy`, `PaymentStrategy`      | Pluggable fee calculation and payment processing                  |
| **Factory**  | `VehicleFactory`, `FeeFactory`, `PaymentFactory` | Encapsulate object creation, decouple callers from concrete types |
| **Template** | `Vehicle` (abstract), `Gate` (abstract)          | Shared structure with type-specific subclasses                    |

---

## Concurrency Model

```
┌──────────────────────────────────────────────────────────────────────┐
│                          ParkingEngine                                │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │  Map<spotId, ReentrantLock> parkingLotLocks                   │  │
│  │                                                                │  │
│  │  Entry Gate 1 ──┐                                             │  │
│  │  Entry Gate 2 ──┼── parkVehicle() ──┐                         │  │
│  │  Entry Gate 3 ──┘                   │                         │  │
│  │                                     ▼                         │  │
│  │                          findAvailableSpot()                   │  │
│  │                                     │                         │  │
│  │                          ┌──────────┴──────────┐              │  │
│  │                          ▼                     ▼              │  │
│  │                    lock(Spot A)           lock(Spot B)         │  │
│  │                    [PARALLEL]            [PARALLEL]            │  │
│  │                          │                     │              │  │
│  │                  re-check available    re-check available     │  │
│  │                   ├── yes → park        ├── yes → park        │  │
│  │                   └── no  → retry       └── no  → retry       │  │
│  │                                                                │  │
│  │  Exit Gate 1 ───── unparkVehicle() ── lock(spot) ── pay+free  │  │
│  │  Exit Gate 2 ───── unparkVehicle() ── lock(spot) ── pay+free  │  │
│  │  Exit Gate 3 ───── unparkVehicle() ── lock(spot) ── pay+free  │  │
│  └────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────┘
```

**Key concurrency properties:**

- Different spots can be parked/unparked in **parallel** (different locks)
- Same spot operations are **serialized** (same lock)
- Optimistic find → pessimistic lock → re-check pattern avoids holding locks during search
- Retry loop (max 3) handles races where a spot is taken between find and lock

---

## Time Complexity

| Operation           | Complexity | Notes                                                      |
| ------------------- | ---------- | ---------------------------------------------------------- |
| Park Vehicle        | O(s)       | Linear scan of all spots to find available match           |
| Unpark Vehicle      | O(1)       | Direct ticket lookup via `ConcurrentHashMap`               |
| Find Available Spot | O(f × s)   | Iterate floors × spots, filtering by availability and type |
| Add Floor/Spot      | O(1)       | `ConcurrentHashMap.putIfAbsent` / `computeIfPresent`       |

Where: f = number of floors, s = spots per floor

---

## Potential Improvements

### Design Enhancements

1. **Give Gates a Role** — Currently `Gate` is a passive model stored in the repository but not used in any flow. Gates should be the entry point for park/unpark requests, with `gateId` recorded on tickets for traceability

2. **Interface-Driven Services** — Extract `IParkingService`, `ITicketService`, `IPaymentService` interfaces so `ParkingEngine` depends on abstractions, improving testability and satisfying Dependency Inversion

3. **Spot Assignment Strategy** — Extract spot selection logic into a `SpotAssignmentStrategy` interface (e.g., `NearestToGateStrategy`, `FillFloorByFloorStrategy`, `EvenDistributionStrategy`)

### Concurrency Enhancements

4. **Unify Locking Model** — Currently both `AtomicReference.compareAndSet` in `ParkingSpot` and `ReentrantLock` in `ParkingEngine` protect the same state transition. Choose one: CAS-only (lock-free with retry) or lock-only (simpler reasoning)

5. **Make `parkedVehicle` Volatile** — When using `AtomicReference` for state, the `parkedVehicle` field also needs visibility guarantees across threads

### Data Model

6. **Store `FeeType` Instead of Strategy on Ticket** — `Ticket` currently holds a `FeeCalculationStrategy` instance. Storing `FeeType` and resolving the strategy at calculation time makes `Ticket` serializable

7. **Vehicle Subclass Hierarchy** — `Car`, `Bike`, `Others` add no behavior over `Vehicle`. Consider removing the hierarchy and using `Vehicle` directly unless subclass-specific behavior is planned

### Performance

8. **Indexed Spot Lookup** — Maintain a per-`VehicleType` available-spot queue (e.g., `ConcurrentLinkedQueue`) for O(1) spot finding instead of O(f × s) full scan

9. **Capacity Counters** — Track available spot counts per type with `AtomicInteger` for fast full-lot detection without scanning

### Robustness

10. **Custom Exceptions** — Replace generic `IllegalStateException` with domain exceptions (`ParkingFullException`, `InvalidTicketException`, `PaymentFailedException`)

11. **Input Validation** — Validate vehicle number format, reject duplicate active vehicles, guard against null parameters

---

## How to Run

```bash
cd /path/to/lld
javac multigateparklinglot/src/**/*.java multigateparklinglot/src/**/**/*.java multigateparklinglot/src/**/**/**/*.java
java multigateparklinglot.src.MultiGateParkingLot
```

Runs the test suite with sequential and concurrent parking tests across multiple simulated gates.
