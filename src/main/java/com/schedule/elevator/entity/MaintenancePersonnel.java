package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 维保人员信息表
 */
@Data
@TableName("maintenance_personnel")
public class MaintenancePersonnel {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id; // 维保人员ID

    @TableField("name")
    private String name; // 姓名

    @TableField("phone")
    private String phone; // 手机号

    @TableField("company")
    private String company; // 维保单位名称（冗余字段）

    @TableField("maintenance_unit_id")
    private Long maintenanceUnitId; // 维保单位ID（关联 maintenance_units 表）

    @TableField("maintenance_team_id")
    private Long maintenanceTeamId; // 维保组ID（可关联 future maintenance_teams 表）

    @TableField("status")
    private Integer status; // 在岗状态：1=在岗，0=离岗

    @TableField(value = "created_at")
    private LocalDateTime createdAt; // 创建时间

    @TableField(value = "updated_at")
    private LocalDateTime updatedAt; // 更新时间
}
