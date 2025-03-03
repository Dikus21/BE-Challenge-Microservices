package com.example.scheduler.service.impl;

import com.example.scheduler.entity.ConsumerSubscription;
import com.example.scheduler.repository.SubscriptionRepository;
import com.example.scheduler.service.KafkaConsumerService;
import com.example.scheduler.utils.TemplateResponse;
import com.example.scheduler.utils.kafka.dynamic.KafkaListenerInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class KafkaConsumerImpl implements KafkaConsumerService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private TemplateResponse templateResponse;
    @Autowired
    private KafkaListenerInitializer kafkaListenerInitializer;

    @Override
    public Map<Object, Object> subscribe(ConsumerSubscription request) {
        if (request.getTopicName() == null) return templateResponse.error("Topic name is null");
        Optional<ConsumerSubscription> checkDataDBSubscription = subscriptionRepository.findByTopicName(request.getTopicName());
        if (checkDataDBSubscription.isPresent()) return templateResponse.error("You have subscribe to this topic");
        ConsumerSubscription consumerSubscription = subscriptionRepository.save(request);
        kafkaListenerInitializer.init();
        return templateResponse.success(consumerSubscription);
    }

    @Override
    public Map<Object, Object> unsubcribe(ConsumerSubscription request) {
        if (request.getTopicName() == null) return templateResponse.error("Topic name is null");
        Optional<ConsumerSubscription> checkDataDBSubscription = subscriptionRepository.findByTopicName(request.getTopicName());
        if (!checkDataDBSubscription.isPresent()) return templateResponse.error("You have not subscribe to this topic");
        checkDataDBSubscription.get().setDeletedDate(new Date());
        kafkaListenerInitializer.init();
        return templateResponse.success(subscriptionRepository.save(request));
    }

}
