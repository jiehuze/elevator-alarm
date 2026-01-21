package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ElevatorAgeStatisticsDTO {
    @ExcelProperty("使用年限")
    private String ageRange;          // 使用年限分类

    @ExcelProperty("电梯总数")
    private Long elevatorCount;       // 电梯总数

    @ExcelProperty("故障总数")
    private Long faultCount;          // 故障总数

    @ExcelProperty("困人故障")
    private Long trappedFaultCount;   // 困人故障数

    @ExcelProperty("非困人故障")
    private Long nonTrappedFaultCount; // 非困人故障数

    @ExcelProperty("其他")
    private Long otherFaultCount;     // 其他故障数

    @ExcelProperty("故障率")
    private String faultRate;         // 故障率
}
