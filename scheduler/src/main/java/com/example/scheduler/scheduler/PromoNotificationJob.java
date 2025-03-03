package com.example.scheduler.scheduler;

import com.example.scheduler.utils.kafka.KafkaProducer;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class PromoNotificationJob implements Job {
    @Autowired
    private KafkaProducer kafkaProducer;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String promoMessage = "Today's promo special";
        kafkaProducer.sendPromoNotification(promoMessage + " " + new Date());
    }
}
