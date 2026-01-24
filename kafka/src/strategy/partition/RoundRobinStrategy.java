package strategy.partition;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinStrategy implements IPartitionStrategy {
    private AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public int getPartition(String key, int numberOfPartitions) {
        return currentIndex.getAndUpdate((val) -> {
            return (val + 1) % numberOfPartitions;
        });
    }

}
