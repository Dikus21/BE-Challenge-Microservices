package com.example.scheduler.config;

import com.example.scheduler.scheduler.PromoNotificationJob;
import com.example.scheduler.scheduler.SimpleJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzSchedulerConfig {

    @Bean
    public JobDetail simpleJobDetail() {
        return JobBuilder.newJob(SimpleJob.class)
                .withIdentity("my_topic")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger simpleJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(5)
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(simpleJobDetail())
                .withIdentity("my_topic")
                .withSchedule(scheduleBuilder)
                .build();
    }

    @Bean
    public JobDetail promoNotificationJob() {
        return JobBuilder.newJob(PromoNotificationJob.class)
                .withIdentity("PromoNotificationJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger promoNotificationJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(simpleJobDetail())
                .withIdentity("PromoNotificationJob")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 12 * * ?"))
                .build();
    }
}
