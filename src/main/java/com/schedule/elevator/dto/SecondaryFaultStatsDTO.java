package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SecondaryFaultStatsDTO {
    @ExcelProperty("县区")
    private String district;

    @ExcelProperty("数量")
    private Integer count;

    @ExcelProperty("电梯救援识别码")
    private String rescueCode;

    @ExcelProperty("注册代码")
    private String registerCode;

    @ExcelProperty("接警时间")
    private String alarmTime;

    @ExcelProperty("事件类型")
    private String eventType;

    @ExcelProperty("电梯地址")
    private String elevatorAddress;

    @ExcelProperty("维保单位")
    private String maintenanceUnit;

    @ExcelProperty("物业名称")
    private String usingUnit;

    @ExcelProperty("故障原因")
    private String faultReason;
}
