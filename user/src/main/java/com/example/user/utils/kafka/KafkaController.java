package com.example.user.utils.kafka;

import com.example.user.utils.kafka.dinamis.KafkaTes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user-login/kafka/")
public class KafkaController {

    private final KafkaProducer producer;

    public KafkaController(KafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/publish")
    public ResponseEntity<Map> writeMessageToTopic(@RequestParam("message") String message){
        this.producer.writeMessage(message);
        Map test = new HashMap<>();
        test.put("message", message);
        return new ResponseEntity<Map>(test, HttpStatus.OK);
    }
    @PostMapping("/publish22")
    public void writeMessageToTopic2(@RequestParam("topic") String topic, @RequestParam("message") String message){
        KafkaTes prod = new KafkaTes(topic, message);
        prod.kirim();
    }


}

