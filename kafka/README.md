# Message Queue System (Kafka-like) - Low Level Design

## Problem Statement

Design a **simplified message queue system** inspired by Apache Kafka that enables asynchronous communication between producers and consumers. The system should support topics with multiple partitions, consumer groups for load balancing, and configurable strategies for message routing and partition distribution.

---

## Functional Requirements

1. **Topic & Partition Management**
   - Create topics with configurable number of partitions
   - Messages within a partition maintain **FIFO order**
   - Each partition tracks its own offset

2. **Producer**
   - Publish messages to topics via Broker
   - Route messages to partitions using configurable strategies:
     - **Hashed Partitioning**: Same key always maps to same partition
     - **Round-Robin**: Distribute messages evenly across partitions

3. **Consumer Groups**
   - Consumers with same `groupId` share partitions (load balancing)
   - Each partition assigned to **only one consumer** within a group
   - Track consumed offsets per partition at group level
   - Automatic partition rebalancing when consumers are added

4. **Broker**
   - Central component managing topics and consumer groups
   - Handle topic creation and message publishing
   - Manage consumer subscriptions and partition assignments

---

## Out of Scope

- Consumer rebalancing on consumer departure
- Message persistence / retention policies
- Exactly-once delivery semantics
- Multiple broker support (distributed system)
- Heartbeat mechanism

---

## Project Structure

```
kafka/
├── src/
│   ├── Driver.java
│   ├── broker/
│   │   └── Broker.java
│   ├── consumer/
│   │   └── ConsumerGroup.java
│   ├── message/
│   │   └── Message.java
│   ├── producer/
│   │   └── Producer.java
│   ├── strategy/
│   │   ├── distribution/
│   │   │   ├── IDistributionStrategy.java
│   │   │   └── RoundRobinDistribution.java
│   │   └── partition/
│   │       ├── IPartitionStrategy.java
│   │       ├── HashedPartitionStrategy.java
│   │       └── RoundRobinStrategy.java
│   └── topic/
│       ├── Partition.java
│       └── Topic.java
```

---

## Class Overview

### Core Classes

| Class           | Responsibility                                                      |
| --------------- | ------------------------------------------------------------------- |
| `Message`       | Immutable payload with message content and offset                   |
| `Partition`     | Thread-safe message storage with offset-based retrieval             |
| `Topic`         | Manages partitions and routes messages using partition strategy     |
| `Broker`        | Central coordinator for topics, consumer groups, and publishing     |
| `ConsumerGroup` | Manages consumer-partition assignment, offset tracking, and polling |
| `Producer`      | Client that publishes messages to topics via broker                 |

### Strategy Interfaces

| Interface               | Responsibility                        | Implementations                                 |
| ----------------------- | ------------------------------------- | ----------------------------------------------- |
| `IPartitionStrategy`    | Select partition for a message        | `HashedPartitionStrategy`, `RoundRobinStrategy` |
| `IDistributionStrategy` | Distribute partitions among consumers | `RoundRobinDistribution`                        |

---

## Design Decisions

### Why No Separate `Consumer` Class?

In real Kafka, consumers are **external client applications** that connect to the broker. They are not entities managed inside the broker itself.

In this design:

- Consumers are identified by `consumerId` (string identifier)
- `ConsumerGroup` tracks which consumer owns which partitions and their offsets
- `poll(consumerId)` is exposed by `ConsumerGroup` for message retrieval

### Thread Safety

The implementation uses thread-safe constructs:

- `ConcurrentHashMap` for concurrent access to topics, partitions, and consumer mappings
- `AtomicInteger` for offset management
- `ReadWriteLock` for safe rebalancing operations

### Offset Tracking

Offsets are tracked at the **ConsumerGroup level** per partition (not per consumer). This allows seamless handover if a consumer fails and another takes over its partitions.

---

## Key Flows

### Message Publishing

```
Producer → Broker.publishToTopic(topic, key, message)
         → Topic.addMessage(key, message)
         → IPartitionStrategy.getPartition(key, numPartitions)
         → Partition.appendMessage(message)
```

### Message Consumption

```
ConsumerGroup.poll(consumerId)
  → Get assigned partitions for consumer
  → For each partition: read messages from last consumed offset
  → Increment consumed offset
  → Return messages
```

### Partition Rebalancing

```
ConsumerGroup.rebalance()
  → IDistributionStrategy.rebalance(consumers, partitions)
  → Reassign partitions using round-robin distribution
```

---

## Design Patterns Used

| Pattern                  | Where                                 | Purpose                                     |
| ------------------------ | ------------------------------------- | ------------------------------------------- |
| **Strategy**             | `IPartitionStrategy`                  | Pluggable partition selection algorithms    |
| **Strategy**             | `IDistributionStrategy`               | Pluggable partition distribution algorithms |
| **Dependency Injection** | `Topic`, `ConsumerGroup`              | Inject strategies via constructor           |
| **Factory-like**         | `Broker.addConsumerToConsumerGroup()` | Create/retrieve consumer groups on demand   |

---

## SOLID Principles Applied

| Principle                 | Implementation                                                                      |
| ------------------------- | ----------------------------------------------------------------------------------- |
| **Single Responsibility** | Each class has one focused purpose (e.g., `Partition` only handles message storage) |
| **Open/Closed**           | New partition/distribution strategies can be added without modifying existing code  |
| **Liskov Substitution**   | Strategy implementations are interchangeable                                        |
| **Interface Segregation** | Focused interfaces (`IPartitionStrategy`, `IDistributionStrategy`)                  |
| **Dependency Inversion**  | Classes depend on abstractions (interfaces), not concrete implementations           |

---

## Usage Example

```java
// Create broker and topics
Broker broker = new Broker();
broker.createTopic("orders", 2, new RoundRobinStrategy());
broker.createTopic("notifications", 3, new HashedPartitionStrategy());

// Add consumers to groups
broker.addConsumerToConsumerGroup("group1", "consumer1");
broker.addConsumerToConsumerGroup("group1", "consumer2");

// Subscribe consumer group to topic
broker.subscribe("group1", "orders");

// Publish messages
broker.publishToTopic("orders", "order123", "Order details");

// Poll messages
ConsumerGroup group = broker.getConsumerGroup("group1");
List<Message> messages = group.poll("consumer1");
```

---

## Possible Extensions

- [ ] Add `commitOffset()` for manual offset commits
- [ ] Implement more partition strategies (Sticky, Random)
- [ ] Implement Range-based distribution strategy
- [ ] Add message timestamps and TTL
- [ ] Support batch publishing and consumption
