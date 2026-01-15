package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class OvertimeWorkOrderDTO {
    @ExcelProperty("时间")
    private Date time;  // 完整时间：2026-01-10 10:20:20

    @ExcelProperty("电梯注册代码")
    private String registerCode;

    @ExcelProperty("维保单位")
    private String maintenanceUnitName;

    @ExcelProperty("使用管理单位")
    private String usingUnit;

    @ExcelProperty("区域")
    private String district;

    @ExcelProperty("电梯所在项目")
    private String projectName;

    @ExcelProperty("抵达救援现场用时")
    private Integer timeToArrive;  // 单位：秒

    @ExcelProperty("超时")
    private Integer overtime;  // 超出30分钟的部分，单位：秒
}
