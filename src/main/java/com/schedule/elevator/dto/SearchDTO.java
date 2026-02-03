package com.schedule.elevator.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@ApiModel("工单查询条件")
public class SearchDTO extends ExportTaskDTO implements Serializable {

    @ApiModelProperty("工单编号")
    private String orderNo;

    @ApiModelProperty("电梯救援码")
    private String rescueCode;

    @ApiModelProperty("电梯名称")
    private String elevatorName;

    @ApiModelProperty("电梯类型")
    private String elevatorType;

    @ApiModelProperty("电梯编号")
    private String elevatorNo;

    @ApiModelProperty("电梯注册码")
    private String registerCode;

    @ApiModelProperty("电梯使用状态")
    private Integer usageStatus;

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

    @ApiModelProperty("使用单位ID")
    private Long usingUnitId;

    @ApiModelProperty("安全员id")
    private String safetyOfficerId;

    @ApiModelProperty("安全员")
    private String safetyOfficerName;

    @ApiModelProperty("安全员电话")
    private String safetyOfficerPhone;

    @ApiModelProperty("工单状态")
    private Integer status;  // 工单状态

    @ApiModelProperty("是否为重大事项")
    private Boolean majorIncident;

    @ApiModelProperty("工单类型ID")
    private String orderType;

    @ApiModelProperty("历史工单")
    private Boolean historyWorkOrder;

    @ApiModelProperty("维保单位ID")
    private Long maintenanceUnitId;

    @ApiModelProperty("维保单位")
    private String maintenanceUnit;

    @ApiModelProperty("维保班组")
    private String maintenanceTeam;

    @ApiModelProperty("维保班组负责人")
    private String maintenanceTeamLeader;

    @ApiModelProperty("维保班组ID")
    private Long maintenanceTeamId; // 维保班组ID

    @ApiModelProperty("维保人")
    private String maintenancePersonnelName;

    @ApiModelProperty("维保人ID")
    private Long maintenancePersonnelId;

    @ApiModelProperty("维保人电话")
    private String maintenancePersonnelPhone;

    @ApiModelProperty("时间范围")
    private String timeRange; // 时间范围

    @ApiModelProperty("导出条件")
    private String queryConditions; // 导出条件

    @ApiModelProperty("维保单位级别,1：一级维保单位，2：二级维保单位")
    private Integer level; // 维保单位级别,1：一级维保单位，2：二级维保单位

    @ApiModelProperty("小区id")
    private Long communityId;

    @ApiModelProperty("报警开始时间")
    private LocalDateTime alarmTimeStart;

    @ApiModelProperty("报警结束时间")
    private LocalDateTime alarmTimeEnd;

    @ApiModelProperty("开始运行时间")
    private LocalDateTime startOperationDateStart;

    @ApiModelProperty("结束运行时间")
    private LocalDateTime startOperationDateEnd;

    @ApiModelProperty("运行年限最小")
    private Integer serviceLifeMin;

    @ApiModelProperty("运行年限最大")
    private Integer serviceLifeMax;

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

    @ApiModelProperty("无维保组")
    private Boolean noMaintenanceTeam;

    @ApiModelProperty("页码，默认1")
    private Integer current = 1;

    @ApiModelProperty("每页大小，默认10")
    private Integer size = 10;
}