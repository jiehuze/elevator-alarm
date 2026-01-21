package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UsingUnitFaultRateDTO {
    @ExcelProperty("序号")
    private Integer idx;              // 序号
    
    @ExcelProperty("使用单位")
    private String usingUnit;         // 使用单位
    
    @ExcelProperty("区域")
    private String district;          // 区域
    
    @ExcelProperty("电梯数")
    private Integer elevatorCount;    // 电梯数
    
    @ExcelProperty("故障数")
    private Integer faultCount;       // 故障数
    
    @ExcelProperty("故障率")
    private String faultRate;         // 故障率（带%符号的字符串）
}
