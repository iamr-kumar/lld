package kafka.src.strategy.distribution;

import java.util.List;
import java.util.Map;

import kafka.src.topic.Partition;

public interface IDistributionStrategy {
    Map<String, List<Partition>> rebalance(List<String> consumers, List<Partition> allPartitions);
}
