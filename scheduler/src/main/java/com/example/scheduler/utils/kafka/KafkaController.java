package com.example.scheduler.utils.kafka;

import com.example.scheduler.entity.ConsumerSubscription;
import com.example.scheduler.service.KafkaConsumerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/kafka")
public class KafkaController {
    private final KafkaConsumerService kafkaConsumerService;

    public KafkaController(KafkaConsumerService kafkaConsumerService) {
        this.kafkaConsumerService = kafkaConsumerService;
    }

    @PostMapping({"/subscribe", "/subscribe/"})
    public ResponseEntity<Map> subcribeTopic(@RequestBody ConsumerSubscription request) {
        return new ResponseEntity<Map>(kafkaConsumerService.subscribe(request), HttpStatus.OK);
    }

    @DeleteMapping({"/unsubscribe", "/unsubscribe/"})
    public ResponseEntity<Map> unsubscribeTopic(@RequestBody ConsumerSubscription request) {
        return new ResponseEntity<Map>(kafkaConsumerService.unsubcribe(request), HttpStatus.OK);
    }
}
