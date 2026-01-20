package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class MaintenanceUnitFaultRateDTO {
    @ExcelProperty("序号")
    private Integer idx;              // 序号

    @ExcelProperty("维保单位")
    private String maintenanceUnit;     // 维保单位

    @ExcelProperty("电梯数")
    private Integer elevatorCount;      // 电梯数

    @ExcelProperty("故障数")
    private Integer faultCount;         // 故障数

    @ExcelProperty("故障率")
    private String faultRate;           // 故障率
}
