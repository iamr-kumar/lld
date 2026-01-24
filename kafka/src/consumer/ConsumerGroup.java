package consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import message.Message;
import strategy.distribution.IDistributionStrategy;
import topic.Partition;

public class ConsumerGroup {
    private final String groupId;
    private Map<String, List<Partition>> consumerToPartitions;
    private final Map<String, AtomicInteger> consumedOffset;
    private final List<String> subscribedTopics;
    private final List<Partition> allPartitions;
    private final IDistributionStrategy distributionStrategy;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ConsumerGroup(String groupId, IDistributionStrategy distributionStrategy) {
        this.groupId = groupId;
        consumerToPartitions = new ConcurrentHashMap<>();
        consumedOffset = new ConcurrentHashMap<>();
        subscribedTopics = new ArrayList<>();
        allPartitions = new ArrayList<>();
        this.distributionStrategy = distributionStrategy;
    }

    public String getGroupId() {
        return groupId;
    }

    public void addConsumer(String consumerId) {
        consumerToPartitions.putIfAbsent(consumerId, new ArrayList<>());
        this.rebalance();
    }

    public void subscribe(String topicName, List<Partition> partitions) {
        if (subscribedTopics.contains(topicName)) {
            return;
        }
        subscribedTopics.add(topicName);
        allPartitions.addAll(partitions);
        for (Partition partition : partitions) {
            consumedOffset.putIfAbsent(partition.getId(), new AtomicInteger(-1));
        }
        this.rebalance();
    }

    public void rebalance() {
        lock.writeLock().lock();
        try {
            if (consumerToPartitions.isEmpty() || allPartitions.isEmpty()) {
                return;
            }
            List<String> consumers = new ArrayList<>(consumerToPartitions.keySet());
            consumerToPartitions = distributionStrategy.rebalance(consumers,
                    allPartitions);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Message> poll(String consumerId) {
        List<Partition> partitions = consumerToPartitions.getOrDefault(consumerId, null);
        if (partitions == null || partitions.isEmpty()) {
            return new ArrayList<>();
        }
        List<Message> messages = new ArrayList<>();
        for (Partition partition : partitions) {
            int lastOffSetCount = consumedOffset.getOrDefault(partition.getId(), new AtomicInteger(-1)).get();
            Optional<Message> messageOpt = partition.getMessageAtOffset(lastOffSetCount + 1);
            while (messageOpt.isPresent()) {
                Message message = messageOpt.get();
                messages.add(message);
                lastOffSetCount = consumedOffset.get(partition.getId()).incrementAndGet();
                messageOpt = partition.getMessageAtOffset(lastOffSetCount + 1);
            }
        }

        return messages;
    }

}
