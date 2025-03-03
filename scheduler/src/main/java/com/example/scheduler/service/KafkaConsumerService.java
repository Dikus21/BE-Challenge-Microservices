package com.example.scheduler.service;

import com.example.scheduler.entity.ConsumerSubscription;

import java.util.Map;

public interface KafkaConsumerService {
    Map<Object, Object> subscribe(ConsumerSubscription request);
    Map<Object, Object> unsubcribe(ConsumerSubscription request);
}
