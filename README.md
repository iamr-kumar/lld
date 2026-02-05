# Low Level Design (LLD) Repository

A collection of **Low Level Design** implementations for common system design interview problems. Each design focuses on SOLID principles, design patterns, clean architecture, and thread-safety.

---

## 📁 Repository Structure

```text
lld/
├── patterns/              # Design pattern implementations
├── trainscheduling/       # Train platform scheduling system
├── kafka/                 # Message queue system
├── cache/                 # Multi-level cache system
├── filesystem/            # In-memory file system
├── fitso/                 # Fitness app booking system
├── meetingscheduler/      # Meeting room scheduler
├── parkinglot/            # Parking lot management
├── tictactoe/             # Tic Tac Toe game
├── bookmyshow/            # Movie ticket booking
├── loggingsystem/         # Logging framework
├── uber/                  # Ride matching simulation
└── uberdriverdispatch/    # driver dispatch module
```

---

## 🎯 System Designs

| Design                                  | Description                                                                        | Key Concepts                                           |
| --------------------------------------- | ---------------------------------------------------------------------------------- | ------------------------------------------------------ |
| [Train Scheduling](./trainscheduling)   | Platform assignment system for railway stations with waiting queue management      | PriorityQueue, ReadWriteLock, Repository Pattern       |
| [Message Queue (Kafka)](./kafka)        | Simplified Kafka-like pub-sub system with topics, partitions, and consumer groups  | Observer Pattern, Strategy Pattern, Partitioning       |
| [Multi-Level Cache](./cache)            | Configurable L1/L2/L3 cache with eviction policies and data promotion              | LRU Eviction, Builder Pattern, Chain of Responsibility |
| [In-Memory File System](./filesystem)   | Unix-like in-memory file system with size tracking and wildcard navigation         | Composite, Strategy, Template Method, Concurrency      |
| [Fitso (Cult Fit)](./fitso)             | Fitness center booking system with slot management and concurrent booking handling | Strategy Pattern, Concurrency, Slot Management         |
| [Meeting Scheduler](./meetingscheduler) | Meeting room scheduling with conflict detection and safe concurrent booking        | Fine-grained locking, TreeSet, Repository Pattern      |
| [Parking Lot](./parkinglot)             | Multi-floor parking with spot allocation, ticketing, and payment processing        | Factory Pattern, State Pattern, Strategy Pattern       |
| [Tic Tac Toe](./tictactoe)              | Classic game with win detection and state management                               | State Pattern, Command Pattern                         |
| [BookMyShow](./bookmyshow)              | Movie ticket booking with seat selection and show management                       | Seat Locking, Transaction Management                   |
| [Logging System](./loggingsystem)       | Configurable logging framework with multiple appenders                             | Singleton, Chain of Responsibility                     |
| [Uber (Ride Matching)](./uber)          | Simplified ride matching with workers, driver locking, and async ride requests     | Strategy, Producer–Consumer, CompletableFuture         |

---

## 🧩 Design Patterns

Standalone implementations of common design patterns in [patterns/](./patterns):

| Pattern                                        | Description                                           |
| ---------------------------------------------- | ----------------------------------------------------- |
| [Singleton](./patterns/singleton)              | Eager, Lazy, Thread-safe, and Static Block variations |
| [Factory](./patterns/factory)                  | Object creation without specifying exact class        |
| [Abstract Factory](./patterns/abstractfactory) | Family of related objects (GUI components)            |
| [Builder](./patterns/builder)                  | Step-by-step complex object construction              |
| [Strategy](./patterns/strategy)                | Interchangeable payment algorithms                    |
| [Observer](./patterns/observer)                | Stock notification system                             |
| [Decorator](./patterns/decorator)              | Pizza toppings with dynamic behavior                  |

---

## 🛠️ Tech Stack

- **Language:** Java 17+
- **Concurrency:** ReentrantLock, ReadWriteLock, ConcurrentHashMap
- **Data Structures:** PriorityQueue, TreeMap, HashMap
- **Testing:** JUnit-style assertions in main methods

---

## 🚀 How to Run

Each project can be compiled and run independently:

```bash
cd <project>/src
javac -d ../out $(find . -name "*.java")
java -cp ../out <MainClass>
```

---

## 📚 Learning Focus

- **SOLID Principles** — Single Responsibility, Open/Closed, Interface Segregation, Dependency Inversion
- **Design Patterns** — Creational, Structural, Behavioral patterns applied in real scenarios
- **Concurrency** — Thread-safe designs using locks, atomic operations, and concurrent collections
- **Clean Architecture** — Separation of concerns with services, repositories, and managers

---

## 📝 License

MIT License - Feel free to use for learning and interview preparation.
