package com.schedule.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@HeadRowHeight(30)
@ContentRowHeight(25)
public class WorkOrderExcel {
    @ExcelProperty("工单编号")
    private String orderNo;

    @ExcelProperty("工单类型")
    private String orderType;

    @ExcelProperty("工单细类")
    private String orderSubType;

    @ExcelProperty("场所")
    private String location;

    @ExcelProperty("电梯注册代码")
    private String registerCode;

    @ExcelProperty("电梯救援识别码")
    private String rescueCode;

    @ExcelProperty("电梯使用管理单位")
    private String usingUnit;

    @ExcelProperty("电梯维保单位")
    private String maintenanceUnit;

    @ExcelProperty("小区项目名")
    private String projectName;

    @ExcelProperty("电梯名称")
    private String elevatorName; // 电梯名称

    @ExcelProperty("区域")
    private String district;

    @ExcelProperty("故障原因")
    private String faultReason;

    @ExcelProperty("是否启动医疗救援")
    private String isMedicalRescueStarted; // 可转为 Boolean，但原始值可能是“是/否”

    @ExcelProperty("是否启动重大事件上报")
    private String isMajorIncidentReported; // 同上

    @ExcelProperty("被困人数")
    @NumberFormat("#")
    private Integer trappedCount;

    @ExcelProperty("受伤人数")
    @NumberFormat("#")
    private Integer injuredCount;

    @ExcelProperty("疑似死亡人数")
    @NumberFormat("#")
    private Integer suspectedDeathCount;

    @ExcelProperty("报警人")
    private String alarmPersonName; // 报警人姓名

    @ExcelProperty("报警人电话")
    private String alarmPersonPhone; // 报警人电话

    @ExcelProperty("维保人员")
    private String maintenancePersonnelName; // 维保人员名称

    @ExcelProperty("维保人员电话")
    private String maintenancePersonnelPhone; // 维保人员电话

    @ExcelProperty("报警时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmTime;

    @ExcelProperty("派单时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dispatchTime;

    @ExcelProperty("到达现场时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTime;

    @ExcelProperty("解救时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rescueTime;

    @ExcelProperty("回访时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime followUpTime;

    @ExcelProperty("办结工单时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closeTime;
}
