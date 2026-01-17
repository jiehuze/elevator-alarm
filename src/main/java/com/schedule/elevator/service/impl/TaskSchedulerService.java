package com.schedule.elevator.service.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TaskSchedulerService {
    @Scheduled(cron = "0 0 20 * * ?")
    public void executeTaskAt8AMReport() {
        System.out.println("Task executed at 1 AM------------");
    }
}
