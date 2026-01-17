package com.schedule.elevator.dto;

import com.schedule.elevator.entity.ExportTask;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExportTaskDTO extends ExportTask {
    private String district;       // 区域
    private String orderNo; // 工单编号
    private LocalDateTime startTime; // 开始日期
    private LocalDateTime endTime;   // 结束日期
}
