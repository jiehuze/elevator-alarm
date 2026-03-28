package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class RepeatedFaultElevatorDTO {
    @ExcelProperty("救援识别码")
    private String rescueCode;

    @ExcelProperty("使用单位")
    private String usingUnit;

    @ExcelProperty("维保单位")
    private String maintenanceUnit;

    @ExcelProperty("电梯品牌")
    private String brand;

    @ExcelProperty("区县")
    private String district;

    @ExcelProperty("故障次数")
    private Integer faultCount;
}
