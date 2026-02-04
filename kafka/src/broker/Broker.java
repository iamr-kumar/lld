package kafka.src.broker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kafka.src.consumer.ConsumerGroup;
import kafka.src.strategy.distribution.RoundRobinDistribution;
import kafka.src.strategy.partition.IPartitionStrategy;
import kafka.src.topic.Partition;
import kafka.src.topic.Topic;

public class Broker {
    private final Map<String, Topic> topics;
    private final Map<String, ConsumerGroup> consumerGroups;

    public Broker() {
        topics = new ConcurrentHashMap<>();
        consumerGroups = new ConcurrentHashMap<>();
    }

    public void createTopic(String topicName, int numPartitions, IPartitionStrategy partitionStrategy) {
        Topic newTopic = new Topic(topicName, numPartitions, partitionStrategy);
        topics.put(topicName, newTopic);
    }

    public ConsumerGroup addConsumerToConsumerGroup(String groupId, String consumerId) {
        ConsumerGroup consumerGroup = consumerGroups.computeIfAbsent(groupId,
                k -> new ConsumerGroup(groupId, new RoundRobinDistribution()));
        consumerGroup.addConsumer(consumerId);
        return consumerGroup;
    }

    public void subscribe(String groupId, String topicName) throws Exception {
        ConsumerGroup consumerGroup = consumerGroups.getOrDefault(groupId, null);
        Topic topic = topics.getOrDefault(topicName, null);
        if (consumerGroup == null || topic == null) {
            throw new Exception("Either Consumer Group or Topic does not exist");
        }
        List<Partition> partitions = topic.getPartitions();
        consumerGroup.subscribe(topicName, partitions);
    }

    public int publishToTopic(String topicName, String key, String message) throws Exception {
        Topic topic = topics.getOrDefault(topicName, null);
        if (topic == null) {
            throw new IllegalArgumentException("Topic does not exist");
        }
        return topic.addMessage(key, message);

    }

}
