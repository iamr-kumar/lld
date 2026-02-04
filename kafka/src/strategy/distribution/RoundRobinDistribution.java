package kafka.src.strategy.distribution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kafka.src.topic.Partition;

public class RoundRobinDistribution implements IDistributionStrategy {
    @Override
    public Map<String, List<Partition>> rebalance(List<String> consumers, List<Partition> partitions) {

        Map<String, List<Partition>> assignment = new ConcurrentHashMap<>();

        for (String consumer : consumers) {
            assignment.put(consumer, new ArrayList<>());
        }

        for (int i = 0; i < partitions.size(); i++) {
            String consumer = consumers.get(i % consumers.size());
            assignment.get(consumer).add(partitions.get(i));
        }

        return assignment;
    }
}
