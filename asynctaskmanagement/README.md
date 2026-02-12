# Async Task Management Library

## Problem Statement

Design and implement an **in-memory asynchronous task management library** that supports:

1. **Task definition** — Define tasks with a name, a `Callable<Object>` work unit, and automatic UUID-based identity
2. **Task dependencies (DAG)** — A task may depend on multiple other tasks; it executes only after all dependencies complete successfully
3. **Circular dependency detection** — Reject dependency additions that would introduce cycles
4. **Failure propagation** — If a dependency fails, all transitive dependents are marked FAILED
5. **Global task queue** — Thread-safe queue holding tasks in READY state, consumed by a pool of worker threads
6. **Async results** — Each task exposes a `CompletableFuture<Object>` for composable async workflows
7. **Graceful shutdown** — Drain in-flight tasks and terminate the thread pool cleanly

---

## Architecture Overview

### File Structure

```text
src/
├── TaskManager.java                     # Demo/driver: submits tasks, wires dependencies, runs engine
├── engine/
│   └── TaskEngine.java                  # Facade: hides all infrastructure, exposes clean client API
├── enums/
│   └── TaskStatus.java                  # Task lifecycle states: NEW, QUEUED, RUNNING, COMPLETED, FAILED, CANCELLED
├── models/
│   ├── Task.java                        # Domain entity: id, name, work, status (AtomicReference), CompletableFuture
│   └── TaskNode.java                    # DAG node: wraps Task with dependency/dependent sets and pending counter
├── observer/
│   ├── ITaskLifecycleListener.java      # Observer interface: onTaskCompleted, onTaskFailed
│   └── DependentListener.java           # Concrete observer: enqueues dependents on success, cascades failure via BFS
├── repository/
│   ├── ITaskRepository.java             # Storage contract: CRUD for tasks and task nodes
│   └── TaskRepository.java             # In-memory implementation using ConcurrentHashMaps
├── services/
│   ├── ITaskSubmissionService.java      # Client-facing contract: addTask, addDependency, getTaskFuture
│   ├── TaskSubmissionService.java       # Submission logic: task creation, dependency validation, cycle detection (DFS)
│   ├── ITaskExecutionService.java       # Execution contract: enqueue, markCompleted, markFailed, failSilently
│   └── TaskExecutionService.java        # State transitions + observer notification
└── worker/
    └── TaskExecutor.java                # Runnable worker: polls queue, executes task, reports result
```

---

## Current Design (How It Works)

### High-Level Flow

1. `TaskEngine` (facade) is created — it internally wires the repository, services, queue, thread pool, and listeners.
2. Client submits tasks via `engine.submitTask(name, callable)` and wires dependencies via `engine.addDependency(taskId, dependencyId)`.
3. `engine.start()` discovers root tasks (no dependencies) via `taskRepository.getRootTaskIds()` and enqueues them.
4. Worker threads (`TaskExecutor`) poll the `ArrayBlockingQueue`, CAS the task status from `QUEUED` → `RUNNING`, and execute the `Callable`.
5. On success, `markTaskCompleted` transitions status to `COMPLETED` and notifies all registered `ITaskLifecycleListener`s.
6. `DependentListener.onTaskCompleted` decrements pending counts on dependents; when a dependent's count hits 0, it's enqueued.
7. On failure, `markTaskFailed` transitions status to `FAILED`, completes the future exceptionally, and notifies listeners.
8. `DependentListener.onTaskFailed` runs an iterative BFS to cascade failure to all transitive dependents using `failTaskSilently` (no listener re-trigger).
9. `engine.shutdown()` calls `executorService.shutdown()` with a 60-second timeout, then `shutdownNow()` if needed.

### Key Design Decisions

- **Lock-free status transitions** — `Task.status` is an `AtomicReference<TaskStatus>` with CAS operations (`compareAndSetStatus`). No `synchronized` blocks on the hot path.
- **DAG via adjacency lists** — `TaskNode` holds `Set<String> dependencies` and `Set<String> dependents` (both `ConcurrentHashMap.newKeySet()`). An `AtomicInteger pendingDependencies` counter enables O(1) readiness checks.
- **Observer-driven decoupling** — State transitions in `TaskExecutionService` fire events; side effects (dependent notification, failure cascade) live in `DependentListener`, not in the service itself.
- **`failTaskSilently` vs `markTaskFailed`** — `markTaskFailed` transitions state AND notifies listeners (external entry point, called by workers). `failTaskSilently` transitions state WITHOUT notifying (internal cascade tool, called by `DependentListener`). This prevents re-entrant listener invocation during failure propagation.
- **Facade hides infrastructure** — Clients interact only with `TaskEngine`. `BlockingQueue`, `TaskRepository`, `ExecutorService`, `TaskExecutor`, `DependentListener` are all invisible.
- **DFS cycle detection** — `TaskSubmissionService.hasCircularDependency` performs a DFS from the dependency back to the task to detect if adding the edge would create a cycle.
- **Root task auto-discovery** — `TaskRepository.getRootTaskIds()` scans for nodes with `pendingDependencies == 0`, so clients don't need to manually enqueue starting tasks.

---

## SOLID Principles Adherence

| Principle                       | Status | Notes                                                                                                                                                                                                                                                                           |
| ------------------------------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Single Responsibility (SRP)** | ✅     | `TaskSubmissionService` handles task/dependency CRUD. `TaskExecutionService` handles state transitions + notification. `DependentListener` handles dependent propagation. `TaskExecutor` handles work execution. `TaskEngine` handles wiring. `TaskRepository` handles storage. |
| **Open/Closed (OCP)**           | ✅     | New lifecycle reactions (logging, metrics, retry) = implement `ITaskLifecycleListener` and register. Zero changes to existing code.                                                                                                                                             |
| **Liskov Substitution (LSP)**   | ✅     | All interfaces (`ITaskRepository`, `ITaskSubmissionService`, `ITaskExecutionService`, `ITaskLifecycleListener`) have clean contracts. Swapping implementations doesn't break callers.                                                                                           |
| **Interface Segregation (ISP)** | ✅     | `ITaskSubmissionService` (3 methods, client-facing) and `ITaskExecutionService` (5 methods, execution-facing) are split. `TaskExecutor` only sees `ITaskExecutionService` — no access to submission APIs.                                                                       |
| **Dependency Inversion (DIP)**  | ⚠️     | Services depend on interfaces (`ITaskRepository`) ✅. Constructor injection throughout ✅. However, `TaskEngine` directly instantiates concrete classes (`new TaskRepository()`, `new TaskExecutionService(...)`) — a Builder would allow external injection.                   |

---

## Design Patterns Used

| Pattern      | Usage                                                                                                                                                                                                                 |
| ------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Observer** | `ITaskLifecycleListener` → `DependentListener`. `TaskExecutionService` fires `onTaskCompleted`/`onTaskFailed` events on state transitions. Listeners react independently — decouples state changes from side effects. |
| **Facade**   | `TaskEngine` hides all internal wiring (queue, repository, executor service, workers, listeners). Client API is 5 methods: `submitTask`, `addDependency`, `getTaskFuture`, `start`, `shutdown`.                       |

### Concurrency Primitives

| Primitive                     | Where                                  | Why                                                                |
| ----------------------------- | -------------------------------------- | ------------------------------------------------------------------ |
| `AtomicReference<TaskStatus>` | `Task.status`                          | Lock-free CAS for status transitions; prevents duplicate execution |
| `AtomicInteger`               | `TaskNode.pendingDependencies`         | Lock-free decrement-and-check for dependency readiness             |
| `ConcurrentHashMap`           | `TaskRepository` maps, `TaskNode` sets | Thread-safe storage without global locks                           |
| `ArrayBlockingQueue`          | Ready queue                            | Thread-safe producer-consumer between services and workers         |
| `volatile`                    | `TaskExecutor.isRunning`               | Visibility guarantee for shutdown flag across threads              |

---

## Demo Scenario

`TaskManager` sets up 4 tasks with dependencies:

```
T1 (no deps)      T2 (no deps)
  │                  │
  ▼                  ▼
T3 (depends on T1) T4 (depends on T2)
```

Expected execution: T1 and T2 run in parallel → T3 starts after T1 completes → T4 starts after T2 completes.

---

## How to Run

From repo root:

```bash
javac asynctaskmanagement/src/**/*.java asynctaskmanagement/src/TaskManager.java
java asynctaskmanagement.src.TaskManager
```

---

## Future Improvements

### Feature Enhancements

1. **Task cancellation** — `TaskStatus.CANCELLED` exists in the enum but `cancelTask()` is not implemented; add cancellation with future propagation
2. **Priority scheduling** — Replace `ArrayBlockingQueue` with `PriorityBlockingQueue` behind a `SchedulingStrategy` interface
3. **Retry with exponential backoff** — Add a `RetryListener` that re-enqueues failed tasks with configurable retry count and delay
4. **Timeout support** — Wrap task execution in `Future.get(timeout)` to prevent tasks from blocking workers indefinitely
5. **Execution metadata** — Track start time, end time, and duration on each `Task` for observability

### Engineering / Design Enhancements

1. **Builder pattern for `TaskEngine`** — Configurable worker count, queue capacity, custom repository, and listener registration via `TaskEngine.builder().workerCount(8).build()`
2. **Strategy pattern for scheduling** — Abstract queue operations behind `SchedulingStrategy` interface to swap FIFO/priority without modifying core logic
3. **Strategy pattern for failure handling** — Abstract failure behavior behind `FailureStrategy` (cascade, retry, skip-and-continue)
4. **State pattern for `Task` status** — Replace ad-hoc CAS guards with explicit state classes (`NewState`, `QueuedState`, `RunningState`) that define legal transitions
5. **Hide `failTaskSilently` from public interface** — Move it to an internal `ITaskStateManager` interface not visible to `TaskExecutor`
6. **Move worker startup to `start()`** — Currently workers are started in `TaskEngine` constructor (spinning on empty queue); start them in `start()` instead
7. **Idempotent `start()`** — Add a `started` flag to prevent double-enqueue of root tasks

### Code Quality

1. **Use `CopyOnWriteArrayList`** for listener list in `TaskExecutionService` — current `ArrayList` is not thread-safe if listeners are added after workers start
2. **Handle `offer()` failure in `enqueueTask`** — If the bounded queue is full, roll back status from `QUEUED` to `NEW` instead of leaving the task stranded
3. **Replace `null` returns with `Optional`** — `getTaskFuture` returns `null` for unknown task IDs; prefer `Optional<CompletableFuture<Object>>` or throw `TaskNotFoundException`
4. **Synchronize `addDependency`** — Current DFS cycle check + graph mutation is not atomic (TOCTOU race under concurrent modifications)
5. **Unit tests** — Test cycle detection, failure cascade, concurrent enqueue/complete, shutdown behavior, and diamond-DAG dedup
