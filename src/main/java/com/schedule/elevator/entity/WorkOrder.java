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

    @TableField("register_code")
    private String registerCode; // 电梯注册码

    @TableField("project_name")
    private String projectName; // 项目名称

    @TableField("project_type")
    private String projectType; // 项目类型,场所

    @TableField("district")
    private String district; // 地区

    @TableField("elevator_address")
    private String elevatorAddress; // 电梯地址

    @TableField("alarm_person_name")
    private String alarmPersonName; // 报警人姓名

    @TableField("alarm_person_phone")
    private String alarmPersonPhone; // 报警人电话

    @TableField("order_type")
    private Integer orderType; // 工单类型 1. 困人工单，2. 故障工单，3. 投诉，4. 咨询

    @TableField("incident_description")
    private String incidentDescription; // 事故描述

    @TableField("rescue_level")
    private Integer rescueLevel; // 救援级别

    @TableField("using_unit")
    private String usingUnit; // 使用单位名称

    @TableField("using_unit_id")
    private Long usingUnitId;   // 使用单位ID

    @TableField("maintenance_unit_id")
    private Long maintenanceUnitId; // 维保单位ID

    @TableField("maintenance_team_id")
    private Long maintenanceTeamId; // 维保班组ID

    @TableField("maintenance_personnel_id")
    private Long maintenancePersonnelId; // 维保人员ID

    @TableField("maintenance_unit_name")
    private String maintenanceUnitName; // 维保单位名称

    @TableField("maintenance_team_name")
    private String maintenanceTeamName; // 维保班组名称

    @TableField("maintenance_personnel_name")
    private String maintenancePersonnelName; // 维保人员名称

    @TableField("maintenance_personnel_phone")
    private String maintenancePersonnelPhone; // 维保人员电话

    @TableField("maintenance_team_leader_phone")
    private String maintenanceTeamLeaderPhone; // 维保班组领导电话

    @TableField("rescue_hotline")
    private String rescueHotline; // 救援热线

    //0:创建工单，1:派单，2:救援人员响应成功，3:回拨安抚，4救援人员到达现场，5:救援人员救援完成，6:救援回访，7：维修回访，8:维修完成，99:结案
    @TableField("status")
    private Integer status;  // 工单状态

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
    private Boolean medicalRescueStarted; // 是否启动医疗救援

    @TableField(value = "time_to_arrive")
    private Long timeToArrive; // 到达现场用时（秒）

    @TableField(value = "rescue_duration")
    private Long rescueDuration; // 救援用时（秒）

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}