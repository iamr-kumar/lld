Here is a **comprehensive, interview-ready problem statement** suitable for an **Uber SDE-2 Low Level Design / Machine Coding round**, derived from the image and expanded to production-level clarity.

---

# 🚘 Uber SDE-2 LLD / Machine Coding Round

## Problem: Design an Asynchronous Task Management Library

You are required to design and implement an **in-memory asynchronous task management library** that allows clients to define tasks, specify dependencies between them, and execute them efficiently using a shared execution framework.

The goal is to evaluate:

- Class design clarity
- Concurrency understanding
- Dependency resolution logic
- Queue management
- Clean and extensible code

---

# 📌 Functional Requirements

## 1. Task Definition

The system should allow users to define a task with:

- `taskId` (unique identifier)
- `taskName`
- `priority` (optional enhancement)
- `Runnable` / `Callable` work unit
- Status tracking:
  - CREATED
  - READY
  - RUNNING
  - SUCCESS
  - FAILED
  - CANCELLED

Each task:

- Executes asynchronously
- Can return a result (optional)
- Can throw exceptions
- Should maintain execution metadata (start time, end time)

---

## 2. Task Dependencies

The system must support **task dependency management**:

- A task may depend on multiple tasks.
- A task should execute **only after all its dependencies complete successfully**.
- If a dependency fails:
  - The dependent task should not execute.
  - It should move to FAILED or SKIPPED state.

### Additional Requirements:

- Detect circular dependencies.
- Reject tasks that introduce cycles.
- Provide ability to query dependency graph.

---

## 3. Global Task Queue

The system should maintain a **global task queue**:

- Only tasks in READY state can enter the queue.
- Tasks become READY when all dependencies complete.
- Queue must be:
  - Thread-safe
  - Capable of handling concurrent producers

- Optional:
  - Support priority ordering
  - FIFO within same priority

---

## 4. Main Task Runner

Design a **Task Runner / Scheduler** that:

- Runs in background
- Picks tasks from global queue
- Executes tasks using a configurable thread pool
- Updates task status
- Notifies dependents when a task completes

The runner should:

- Handle failures gracefully
- Prevent duplicate execution
- Support graceful shutdown
- Ensure no task is lost

---

# 📌 APIs to Design

Design clean APIs such as:

```java
interface TaskManager {
    void submit(Task task);
    void addDependency(String taskId, String dependsOnTaskId);
    TaskStatus getStatus(String taskId);
    void start();
    void shutdown();
}
```

You may add:

- `cancel(taskId)`
- `getResult(taskId)`
- `getExecutionGraph()`

---

# 📌 Non-Functional Requirements

- Thread-safe design
- Scalable to thousands of tasks
- Clean separation of concerns
- Minimal locking (avoid global synchronized blocks)
- Efficient dependency resolution
- O(1) lookup of tasks
- No busy waiting

---

# 📌 Edge Cases to Handle

1. Circular dependency
2. Dependency added after execution started
3. Duplicate task submission
4. Task failure propagation
5. Graceful shutdown with running tasks
6. Task cancellation while queued
7. Concurrent submissions

---

# 📌 Constraints

- In-memory implementation only
- No external databases
- No distributed systems required
- Language: Java / C++ / Go (your choice)
- Must be runnable

---

# 📌 Expected Class-Level Design

You are expected to design classes such as:

- `Task`
- `TaskManager`
- `DependencyGraph`
- `TaskQueue`
- `TaskScheduler`
- `Worker`

Interviewer will evaluate:

- Object modeling
- Use of interfaces
- Extensibility
- Responsibility separation
- Proper use of concurrency primitives

---

# 📌 Optional Enhancements (If Time Permits)

- Task retry with exponential backoff
- Timeout support
- Priority scheduling
- Event listeners (onSuccess, onFailure)
- Batch submission
- Metrics collection

---

# 📌 What Interviewer Is Evaluating

This problem tests:

- Ability to design clean abstractions
- Correct handling of DAG execution
- Concurrency correctness
- Code readability
- Production thinking
- Handling race conditions
- Translating design into working code

---

# 📌 Example Scenario

Given tasks:

```
T1
T2 depends on T1
T3 depends on T1
T4 depends on T2 and T3
```

Expected execution order:

```
T1 → (T2 & T3 in parallel) → T4
```

---

# 📌 Deliverables in Interview

1. Class diagram (whiteboard or verbally)
2. Core data structures
3. Implementation of:
   - Task submission
   - Dependency resolution
   - Execution engine

4. Basic demo in `main()`

---

If you'd like, I can now:

- Provide a **staff-level evaluation rubric**
- Provide a **complete working reference implementation**
- Or simulate how Uber interviewer might grill you on this design\*\*
