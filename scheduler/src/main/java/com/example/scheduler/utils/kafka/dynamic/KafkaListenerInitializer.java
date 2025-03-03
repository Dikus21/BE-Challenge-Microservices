package com.example.scheduler.utils.kafka.dynamic;

import com.example.scheduler.entity.ConsumerSubscription;
import com.example.scheduler.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class KafkaListenerInitializer {
    @Autowired
    private DynamicKafkaListener dynamicKafkaListener;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @PostConstruct
    public void init() {
        List<ConsumerSubscription> subscriptions = subscriptionRepository.findAll();
        if (subscriptions.isEmpty()) return;
        String[] topics = subscriptions.stream()
                .map(ConsumerSubscription::getTopicName).toArray(String[]::new);
        dynamicKafkaListener.startOrUpdateListener(topics);
    }
}
