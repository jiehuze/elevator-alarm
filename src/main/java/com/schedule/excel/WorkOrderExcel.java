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
    // @ExcelProperty(index = 0)
    private String orderNo;

    @ExcelProperty("工单类型")
    // @ExcelProperty(index = 1)
    private String orderType;

    @ExcelProperty("场所")
    // @ExcelProperty(index = 2)
    private String location;

    @ExcelProperty("电梯注册代码")
    // @ExcelProperty(index = 3)
    private String registerCode;

    @ExcelProperty("电梯救援识别码")
    // @ExcelProperty(index = 4)
    private String rescueCode;

    @ExcelProperty("电梯使用管理单位")
    // @ExcelProperty(index = 5)
    private String usingUnit;

    @ExcelProperty("电梯维保单位")
    // @ExcelProperty(index = 6)
    private String maintenanceUnit;

    @ExcelProperty("小区项目名")
    // @ExcelProperty(index = 7)
    private String projectName;

    @ExcelProperty("电梯名称")
    // @ExcelProperty(index = 8)
    private String elevatorName; // 电梯名称

    @ExcelProperty("区域")
    // @ExcelProperty(index = 9)
    private String district;

    @ExcelProperty("故障原因")
    // @ExcelProperty(index = 10)
    private String faultReason;

    @ExcelProperty("是否启动医疗救援")
    // @ExcelProperty(index = 11)
    private String isMedicalRescueStarted; // 可转为 Boolean，但原始值可能是"是/否"

    @ExcelProperty("是否启动重大事件上报")
    // @ExcelProperty(index = 12)
    private String isMajorIncidentReported; // 同上

    @ExcelProperty("被困人数")
    // @ExcelProperty(index = 13)
    @NumberFormat("#")
    private Integer trappedCount;

    @ExcelProperty("受伤人数")
    // @ExcelProperty(index = 14)
    @NumberFormat("#")
    private Integer injuredCount;

    @ExcelProperty("疑似死亡人数")
    // @ExcelProperty(index = 15)
    @NumberFormat("#")
    private Integer suspectedDeathCount;

    @ExcelProperty("报警人")
    // @ExcelProperty(index = 16)
    private String alarmPersonName; // 报警人姓名

    @ExcelProperty("报警人电话")
    // @ExcelProperty(index = 17)
    private String alarmPersonPhone; // 报警人电话

    @ExcelProperty("维保人员")
    // @ExcelProperty(index = 18)
    private String maintenancePersonnelName; // 维保人员名称

    @ExcelProperty("维保人员电话")
    // @ExcelProperty(index = 19)
    private String maintenancePersonnelPhone; // 维保人员电话

    @ExcelProperty("报警时间")
    // @ExcelProperty(index = 20)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmTime;

    @ExcelProperty("派单时间")
    // @ExcelProperty(index = 21)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dispatchTime;

    @ExcelProperty("到达现场时间")
    // @ExcelProperty(index = 22)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTime;

    @ExcelProperty("解救时间")
    // @ExcelProperty(index = 23)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rescueTime;

    @ExcelProperty("维修完成时间")
    // @ExcelProperty(index = 24)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime repairTime;

    @ExcelProperty("回访时间")
    // @ExcelProperty(index = 25)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime followUpTime;

    @ExcelProperty("办结工单时间")
    // @ExcelProperty(index = 26)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closeTime;

    @ExcelProperty("到达时长")
    // @ExcelProperty(index = 26)
    private String timeToArrive;

    @ExcelProperty("处理时长")
    // @ExcelProperty(index = 27)
    private String timeToHandle;
}
