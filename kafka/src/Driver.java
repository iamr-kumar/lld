package kafka.src;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kafka.src.broker.Broker;
import kafka.src.consumer.ConsumerGroup;
import kafka.src.message.Message;
import kafka.src.strategy.partition.HashedPartitionStrategy;
import kafka.src.strategy.partition.IPartitionStrategy;
import kafka.src.strategy.partition.RoundRobinStrategy;

public class Driver {

    public static void main(String[] args) {
        System.out.println("Kafka Driver is running...");

        Map<String, ConsumerGroup> consumerGroups = new HashMap<>();
        Map<String, List<String>> consumerIds = new HashMap<>();

        Broker broker = new Broker();
        IPartitionStrategy roundRobinStrategy = new RoundRobinStrategy();
        IPartitionStrategy hashedStrategy = new HashedPartitionStrategy();

        broker.createTopic("orders", 2, roundRobinStrategy);
        broker.createTopic("notifications", 3, hashedStrategy);

        ConsumerGroup consumerGroup1 = broker.addConsumerToConsumerGroup("group1", "consumer1");
        ConsumerGroup consumerGroup2 = broker.addConsumerToConsumerGroup("group1", "consumer2");
        ConsumerGroup consumerGroup3 = broker.addConsumerToConsumerGroup("group2", "consumer3");

        consumerGroups.putIfAbsent(consumerGroup1.getGroupId(), consumerGroup1);
        consumerGroups.putIfAbsent(consumerGroup2.getGroupId(), consumerGroup2);
        consumerGroups.putIfAbsent(consumerGroup3.getGroupId(), consumerGroup3);

        consumerIds.put("group1", List.of("consumer1", "consumer2"));
        consumerIds.put("group2", List.of("consumer3"));

        try {
            broker.subscribe("group1", "orders");
            broker.subscribe("group2", "notifications");

            int offset1 = broker.publishToTopic("orders", "order123", "Order details for order123");
            System.out.println("Message published to partition: " + offset1);

            int offset2 = broker.publishToTopic("notifications", "user456", "Notification for user456");
            System.out.println("Message published to partition: " + offset2);

            int offset3 = broker.publishToTopic("orders", "order789", "Order details for order789");
            System.out.println("Message published to partition: " + offset3);

            int offset4 = broker.publishToTopic("notifications", "user123", "Notification for user123");
            System.out.println("Message published to partition: " + offset4);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Poll messages for each consumer
        for (String groupId : consumerIds.keySet()) {
            ConsumerGroup group = consumerGroups.get(groupId);
            for (String consumerId : consumerIds.get(groupId)) {
                List<Message> messages = group.poll(consumerId);
                if (!messages.isEmpty()) {
                    for (Message msg : messages) {
                        System.out.println("Consumer " + consumerId + " received message: " + msg.getMessage()
                                + " at offset " + msg.getOffset());

                    }
                } else {
                    System.out.println("No new messages for Consumer " + consumerId);
                }
            }
        }
    }
}