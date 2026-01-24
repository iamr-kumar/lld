package producer;

import broker.Broker;

public class Producer {
    private final Broker broker;

    public Producer(Broker broker) {
        this.broker = broker;
    }

    public void publish(String topic, String key, String value) throws Exception {
        broker.publishToTopic(topic, key, value);
    }

}
