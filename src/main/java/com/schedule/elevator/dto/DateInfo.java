package com.schedule.elevator.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DateInfo {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String name; // 第几季度
    private int number; // 季度：1-4; 年： 2025-现在

    public DateInfo(LocalDateTime startTime, LocalDateTime endTime, String name, int number) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.number = number;
    }
}
