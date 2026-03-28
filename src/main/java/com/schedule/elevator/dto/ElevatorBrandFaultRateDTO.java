package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ElevatorBrandFaultRateDTO {
    @ExcelProperty("序号")
    private Integer idx;              // 序号

    @ExcelProperty("电梯品牌")
    private String brand;             // 电梯品牌

    @ExcelProperty("数量")
    private Integer elevatorCount;    // 电梯数量

    @ExcelProperty("故障数")
    private Integer faultCount;       // 故障数

    @ExcelProperty("故障率")
    private String faultRate;         // 故障率（带%符号的字符串）

    @ExcelProperty("总故障数")
    private Integer totalFaultCount;  // 总故障数
}
