# In-Memory File System

## Problem Statement

Design and implement an **In-Memory File System** that mimics the behavior of a Unix/Linux file system. The system should support basic file system operations like creating directories, navigating through the file system, and managing files with O(1) size calculations.

### Functional Requirements

1. **mkdir** — Create directories at a given path, supporting nested creation (`mkdir -p`)
2. **cd** — Change current working directory using absolute/relative paths with `.` and `..` support
3. **pwd** — Print the absolute path of the current working directory
4. **touch** — Create a file at the specified path
5. **File Operations** — Read and write file content with size tracking
6. **rm/delete** — Remove files or directories (with recursive deletion support)
7. **Wildcard Support** — Navigate using patterns like `/home/*/documents`

### Non-Functional Requirements

- Thread-safe for concurrent file/directory operations
- O(1) directory size calculation using propagated size updates
- Handle edge cases: invalid paths, name conflicts, null inputs
- Clean OOP design with appropriate design patterns

---

## Architecture Overview

### File Structure

```
src/
├── entities/
│   ├── FileSystemEntity.java   # Abstract base class for files and directories
│   ├── Directory.java          # Directory with ConcurrentHashMap children, ReentrantLock
│   └── File.java               # File with content, ReentrantReadWriteLock for R/W
├── services/
│   ├── IFileService.java       # File system operations interface
│   └── FileService.java        # Main orchestrator with path resolution
└── strategy/
    ├── IPathResolutionStrategy.java      # Path resolution interface
    ├── AbstractResolutionStrategy.java   # Shared segment resolution logic
    ├── AbsolutePathResolutionStrategy.java  # Handles paths starting with /
    ├── RelativePathStrategy.java         # Handles relative paths
    └── WildcardResolutionStrategy.java   # Handles * and ? patterns
```

---

## Design Highlights

### Strong Points

**1. Composite Design Pattern**

- `FileSystemEntity` is the abstract component with shared behavior (name, parent, path, size)
- `File` is a leaf node with content storage
- `Directory` is a composite that contains children (both files and directories)
- Enables uniform treatment of files and directories in path operations

**2. Strategy Pattern for Path Resolution**

- `IPathResolutionStrategy` interface defines path resolution contract
- Three concrete strategies:
  - `AbsolutePathResolutionStrategy` — Paths starting with `/`
  - `RelativePathStrategy` — Paths relative to current directory
  - `WildcardResolutionStrategy` — Paths with `*` or `?` patterns
- `FileService` selects strategy based on path characteristics via `supports()` method
- Easily extensible for new path formats (symlinks, environment variables, etc.)

**3. Template Method Pattern**

- `AbstractResolutionStrategy` provides shared `resolveSegments()` implementation
- Handles `.`, `..`, wildcards, and exact matches in a unified way
- Concrete strategies only define `resolvePath()` entry point and `supports()` check

**4. O(1) Directory Size Calculation**

- Each `Directory` maintains `AtomicLong cachedSize`
- Size changes propagate up the tree via `updateSize(delta)` on modifications
- Parent directories are updated recursively when files are added/removed/modified
- No tree traversal needed for `getSize()` — just return cached value

**5. Thread-Safe Concurrency Model**

- `Directory.children` uses `ConcurrentHashMap` for atomic operations
- `Directory` uses `ReentrantLock` for compound operations (create-if-absent)
- `File` uses `ReentrantReadWriteLock` for concurrent reads, exclusive writes
- Size updates use `AtomicLong` for lock-free increments

**6. Wildcard/Regex Support**

- Patterns `*` (any characters) and `?` (single character) supported in paths
- Converted to regex: `*` → `.*`, `?` → `.`
- Returns multiple matches as `List<Directory>` (first match used for `cd`)

**7. Proper Resource Cleanup**

- `delete()` recursively removes all children before removing from parent
- Size updates propagate correctly on deletion (negative delta)
- Parent reference cleared to prevent memory leaks

---

## SOLID Principles Adherence

| Principle                 | Status | Implementation                                                                                              |
| ------------------------- | ------ | ----------------------------------------------------------------------------------------------------------- |
| **Single Responsibility** | ✅     | `FileService` handles operations, `Directory`/`File` manage their own state, strategies handle path parsing |
| **Open/Closed**           | ✅     | New path strategies can be added without modifying `FileService` (just add to strategy list)                |
| **Liskov Substitution**   | ✅     | `File` and `Directory` are interchangeable as `FileSystemEntity` for common operations                      |
| **Interface Segregation** | ✅     | `IFileService` has focused operations; `IPathResolutionStrategy` has only 2 methods                         |
| **Dependency Inversion**  | ⚠️     | `FileService` uses interface for strategies but instantiates concrete strategies internally                 |

---

## Design Patterns Used

| Pattern             | Usage                                                                |
| ------------------- | -------------------------------------------------------------------- |
| **Composite**       | `FileSystemEntity` → `File` / `Directory` hierarchy                  |
| **Strategy**        | `IPathResolutionStrategy` with 3 implementations for path resolution |
| **Template Method** | `AbstractResolutionStrategy.resolveSegments()` shared algorithm      |

---

## Time Complexity

| Operation          | Complexity | Notes                                                  |
| ------------------ | ---------- | ------------------------------------------------------ |
| mkdir              | O(d)       | d = depth of path, each segment lookup is O(1) HashMap |
| cd                 | O(d)       | Path resolution through segments                       |
| pwd                | O(d)       | Walk up parent chain to build path string              |
| touch              | O(d)       | Path resolution + O(1) file creation                   |
| getSize            | O(1)       | Cached size in AtomicLong                              |
| File read/write    | O(1)       | Direct content access with lock                        |
| delete (file)      | O(1)       | Remove from parent's HashMap                           |
| delete (directory) | O(n)       | n = total descendants (recursive deletion)             |
| Wildcard cd        | O(d × c)   | c = average children per directory matching pattern    |

Where: d = path depth, n = number of entities, c = children count

---

## Concurrency Model

```
┌─────────────────────────────────────────────────────────────────┐
│                     Directory                                    │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  ConcurrentHashMap<String, FileSystemEntity> children   │    │
│  │  ReentrantLock lock (for compound operations)           │    │
│  │  AtomicLong cachedSize                                  │    │
│  │                                                          │    │
│  │  Thread 1: getChild("file.txt")     ── O(1) lock-free   │    │
│  │  Thread 2: getChild("other.txt")    ── O(1) lock-free   │    │
│  │  Thread 3: createFileIfAbsent()     ── Acquires lock    │    │
│  │  Thread 4: getOrCreateDirectory()   ── Waits for lock   │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                        File                                      │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  ReentrantReadWriteLock rwLock                          │    │
│  │                                                          │    │
│  │  Thread 1: getContent() ──┬── Read lock (shared)        │    │
│  │  Thread 2: getContent() ──┘                              │    │
│  │  Thread 3: setContent() ──── Write lock (exclusive)     │    │
│  │                               [Threads 1,2 blocked]      │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

---

## Potential Improvements

### Design Enhancements

1. **Dependency Injection for Strategies** — Inject strategies via constructor instead of hardcoding, improving testability and adhering to DIP

2. **Factory Pattern for Entities** — Add `FileSystemEntityFactory` to centralize creation logic and enforce naming rules

3. **Observer Pattern for Events** — Notify listeners on file changes (useful for watch functionality, sync)

4. **Visitor Pattern for Operations** — Enable operations like `find`, `du`, `tree` without modifying entity classes

### Missing Features

5. **ls Command** — List directory contents with optional flags (-l, -a, -R)

6. **mv/rename Command** — Move or rename files and directories

7. **cp Command** — Copy files and directories

8. **find Command** — Search by name pattern, size, or modification time

9. **Symbolic Links** — Support symlinks with a new `SymLink` entity type

10. **Permissions System** — Add read/write/execute permissions per entity

### Performance Optimizations

11. **Path Caching** — Cache resolved paths to avoid repeated parsing

12. **Lazy Size Calculation Option** — For very deep trees, offer lazy calculation mode

13. **Memory-Mapped Content** — For large files, use lazy content loading

### Robustness

14. **Input Validation** — Add comprehensive path validation (max length, invalid characters)

15. **Custom Exceptions** — Create `PathNotFoundException`, `FileExistsException`, `InvalidPathException`

16. **Undo/Redo Support** — Command pattern for reversible operations

### Scalability

17. **Sharding** — Partition file system by top-level directories for distributed scenarios

18. **Persistence Layer** — Add optional persistence to disk with journaling

---

## How to Run

```bash
cd /path/to/lld
javac filesystem/src/**/*.java filesystem/src/FileSystemAPI.java
java filesystem.src.FileSystemAPI
```

Runs the test suite with 15 tests covering basic operations, edge cases, and concurrency.
