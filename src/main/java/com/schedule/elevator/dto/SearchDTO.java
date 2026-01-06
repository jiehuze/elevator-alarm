package com.schedule.elevator.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel("工单查询条件")
public class SearchDTO implements Serializable {

    @ApiModelProperty("工单编号")
    private String orderNo;

    @ApiModelProperty("电梯救援码")
    private String rescueCode;

    @ApiModelProperty("电梯注册码")
    private String registerCode;

    @ApiModelProperty("员工ID")
    private String employeeId;

    @ApiModelProperty("项目名称")
    private String projectName; // 项目名称

    @ApiModelProperty("项目类型")
    private String projectType;

    @ApiModelProperty("电梯地址")
    private String elevatorAddress; // 电梯地址

    @ApiModelProperty("报警人姓名")
    private String alarmPersonName;

    @ApiModelProperty("报警人电话")
    private String alarmPersonPhone;

    @ApiModelProperty("使用单位")
    private String usingUnit;

    @ApiModelProperty("工单状态")
    private Byte status;

    @ApiModelProperty("是否为重大事项")
    private Boolean majorIncident;

    @ApiModelProperty("工单类型ID")
    private String orderType;

    @ApiModelProperty("维保单位ID")
    private Long maintenanceUnitId;

    @ApiModelProperty("报警开始时间")
    private LocalDateTime alarmTimeStart;

    @ApiModelProperty("报警结束时间")
    private LocalDateTime alarmTimeEnd;

    @ApiModelProperty("创建开始时间")
    private LocalDateTime createTimeStart;

    @ApiModelProperty("创建结束时间")
    private LocalDateTime createTimeEnd;

    @ApiModelProperty("区域")
    private String district;

    @ApiModelProperty("未完成")
    private Boolean unfinished;

    @ApiModelProperty("时间排序")
    private String timeOrder;

    @ApiModelProperty("救援码排序")
    private String rescueCodeOrder;

    @ApiModelProperty("页码，默认1")
    private Integer current = 1;

    @ApiModelProperty("每页大小，默认10")
    private Integer size = 10;
}