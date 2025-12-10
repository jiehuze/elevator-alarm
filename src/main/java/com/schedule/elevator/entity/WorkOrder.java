package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报警工单信息
 */
@Data
@Accessors(chain = true)
@TableName(value = "work_order", autoResultMap = true)
public class WorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("alarm_source")
    private String alarmSource;          // 报警来源

    @TableField("employee_id")
    private String employeeId;           // 报警员工id

    @TableField("alarm_time")
    private LocalDateTime alarmTime; // 报警时间

    @TableField("rescue_code")
    private String rescueCode; // 救援识别码

    @TableField("project_name")
    private String projectName; // 项目名称

    @TableField("elevator_address")
    private String elevatorAddress; // 电梯地址

    @TableField("alarm_person_name")
    private String alarmPersonName; // 报警人姓名

    @TableField("alarm_person_phone")
    private String alarmPersonPhone; // 报警人电话

    @TableField("order_type")
    private String orderType; // 工单类型 困人工单，故障工单，投诉，咨询

    @TableField("incident_description")
    private String incidentDescription; // 事故描述

    @TableField("rescue_level")
    private Byte rescueLevel; // 救援级别

    @TableField("maintenance_unit_id")
    private Long maintenanceUnitId; // 维保单位ID

    @TableField("maintenance_team_id")
    private Long maintenanceTeamId; // 维保班组ID

    @TableField("rescue_hotline")
    private String rescueHotline; // 救援热线

    @TableField("status")
    private Byte status;  // 工单状态

    @TableField("injured_count")
    private Integer injuredCount; // 受伤人数

    @TableField("trapped_count")
    private Integer trappedCount; // 被困人数

    @TableField("suspected_death_count")
    private Integer suspectedDeathCount; // 疑似死亡人数

    @TableField("is_reported")
    private Boolean reported; // 是否上报

    @TableField("is_major_incident")
    private Boolean majorIncident; // 是否为重大事项 0-否，1-是

    @TableField("is_medical_rescue_started")
    private Boolean medicalRescueStarted = false; // 是否启动医疗救援

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}