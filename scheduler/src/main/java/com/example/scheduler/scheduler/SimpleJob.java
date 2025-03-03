package com.example.scheduler.scheduler;

import com.example.scheduler.utils.kafka.KafkaProducer;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class SimpleJob implements Job {
    @Autowired
    private KafkaProducer kafkaProducer;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String promoMessage = "My Topic Remainder";
        kafkaProducer.sendPromoNotification(promoMessage + " " + new Date());
    }
}
