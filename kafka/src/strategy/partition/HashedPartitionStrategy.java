package kafka.src.strategy.partition;

public class HashedPartitionStrategy implements IPartitionStrategy {
    @Override
    public int getPartition(String key, int numberOfPartitions) {
        int hash = key.hashCode();
        return Math.abs(hash) % numberOfPartitions;
    }
}
