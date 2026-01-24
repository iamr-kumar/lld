package strategy.partition;

public interface IPartitionStrategy {
    int getPartition(String key, int numberOfPartitions);
}
