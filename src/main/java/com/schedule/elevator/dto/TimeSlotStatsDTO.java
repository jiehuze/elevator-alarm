package com.schedule.elevator.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TimeSlotStatsDTO implements Serializable {
    private String timeSlot;   // 如 "0-2"
    private Integer count;     // 故障次数
}
