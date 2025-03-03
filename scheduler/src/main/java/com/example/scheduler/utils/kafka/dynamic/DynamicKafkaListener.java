package com.example.scheduler.utils.kafka.dynamic;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class DynamicKafkaListener {
    private final ConcurrentKafkaListenerContainerFactory<String, String> factory;
    private ConcurrentMessageListenerContainer<String, String> container;

    public DynamicKafkaListener(ConcurrentKafkaListenerContainerFactory<String, String> factory) {
        this.factory = factory;
    }

    public void startOrUpdateListener(String... topics) {
        if (container != null && container.isRunning()) {
            container.stop();
        }
        container = factory.createContainer(topics);
        container.setupMessageListener((MessageListener<String, String>) record -> {
            System.out.println("Received: " + record.value());
        });
        container.start();
    }

    public void stopListener() {
        if (container != null) {
            container.stop();
        }
    }
}
