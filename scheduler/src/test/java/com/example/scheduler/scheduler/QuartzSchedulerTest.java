package com.example.scheduler.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class QuartzSchedulerTest {
    @Autowired
    private Scheduler scheduler;

    @Test
    public void testSchedulerIsRunning() throws SchedulerException {
        assertTrue(scheduler.isStarted());
    }

    @Test
    public void testJobIsScheduled() throws SchedulerException {
        JobKey jobKey = JobKey.jobKey("SimpleJob");
        assertTrue(scheduler.checkExists(jobKey));
    }
}
