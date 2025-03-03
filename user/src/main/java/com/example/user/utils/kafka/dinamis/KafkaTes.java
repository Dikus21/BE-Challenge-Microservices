package com.example.user.utils.kafka.dinamis;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaTes {
    Properties props = new Properties();
    String topic = "my-topic";
    String message = "Hello, Kafka!";

    public KafkaTes(String topic, String message) {

        this.topic = topic;
        this.message = message;
    }

    ;


    public void kirim(){
        props = new Properties();
        props.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);


        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);

        producer.send(record);
        System.out.println("topik saya :" + topic + message);
    }


}



