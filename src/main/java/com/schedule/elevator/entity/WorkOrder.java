package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName(value = "work_order", autoResultMap = true)
public class WorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

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

    @TableField("order_type_id")
    private Integer orderTypeId;

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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}