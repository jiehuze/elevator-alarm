package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
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
    private String alarmSource;          // 报警来源
    private String creatorJobNumber;     // 创建人工号
    /**
     * 是否为重大事项：0-否，1-是
     */
    @TableField("is_major_incident")
    private Boolean isMajorIncident;

    @TableField("alarm_time")
    private LocalDateTime alarmTime;

    @TableField("elevator_code")
    private String elevatorCode;

    @TableField("register_code")
    private String registerCode;

    @TableField("alarm_person_name")
    private String alarmPersonName;

    @TableField("alarm_person_phone")
    private String alarmPersonPhone;

    @TableField("elevator_address")
    private String elevatorAddress;

    @TableField("using_unit")
    private String usingUnit;

    @TableField("safety_officer")
    private String safetyOfficer;

    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 工单类型: 困人工单，故障工单，投诉，咨询
     */
    @TableField("order_type")
    private String orderType;

    @TableField("incident_description")
    private String incidentDescription;

    @TableField("rescue_level")
    private Byte rescueLevel; // TINYINT → Byte

    @TableField("rescue_unit")
    private String rescueUnit;

    @TableField("unit_fixed_phone")
    private String unitFixedPhone;

    @TableField("maintenance_worker")
    private String maintenanceWorker;

    @TableField("worker_phone")
    private String workerPhone;

    @TableField("handler_name")
    private String handlerName;

    @TableField("handler_phone")
    private String handlerPhone;

    @TableField("rescue_hotline")
    private String rescueHotline;

    @TableField("status")
    private Byte status; // TINYINT: 0=待处理, 1=处理中, 2=已关闭

    @TableField("is_reported")
    private Boolean isReported;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}