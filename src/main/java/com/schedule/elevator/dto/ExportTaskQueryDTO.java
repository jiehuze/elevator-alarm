package com.schedule.elevator.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExportTaskQueryDTO {
    private String taskName;       // 任务名称
    private String exportType;      // 导出类型
    private Integer status;         // 状态
    private String triggerUserId;   // 触发用户ID
    private LocalDateTime startDate; // 开始日期
    private LocalDateTime endDate;   // 结束日期
}
