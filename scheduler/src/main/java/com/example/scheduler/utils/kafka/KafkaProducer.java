package com.example.scheduler.utils.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendPromoNotification(String message) {
        kafkaTemplate.send("promo-notifications", message);
    }
    public void sendMyTopicNotification(String message) {kafkaTemplate.send("my_topic", message); }
}
