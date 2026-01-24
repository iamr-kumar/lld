package topic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import message.Message;
import strategy.partition.IPartitionStrategy;

public class Topic {
    private final String name;
    private final Map<String, Partition> topicPartitions;
    private final IPartitionStrategy partitionStrategy;

    public Topic(String name, int numPartitions, IPartitionStrategy partitionStrategy) {
        this.name = name;
        this.partitionStrategy = partitionStrategy;
        this.topicPartitions = new HashMap<>();
        for (int i = 0; i < numPartitions; i++) {
            String partitionId = name + "-part-" + i;
            topicPartitions.put(partitionId, new Partition(partitionId));
        }
    }

    public int addMessage(String key, String message) {
        int partitionIndex = partitionStrategy.getPartition(key, topicPartitions.size());
        String partitionId = name + "-part-" + partitionIndex;
        Partition partition = topicPartitions.get(partitionId);
        int newOffset = partition.getNextOffset();

        Message msg = new Message(message, newOffset);
        return partition.appendMessage(msg);
    }

    public List<Partition> getPartitions() {
        return new ArrayList<>(topicPartitions.values());
    }

    public List<String> getPartitionIds() {
        return new ArrayList<>(topicPartitions.keySet());
    }
}
