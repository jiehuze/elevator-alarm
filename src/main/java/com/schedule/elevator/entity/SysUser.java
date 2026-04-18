package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("employee_id")
    private String employeeId;           // 报警员工id

    @TableField("roles")
    private String roles;

    @TableField("description")
    private String description;

    @TableField("maintenance_unit_id")
    private Long maintenanceUnitId;

    @TableField(exist = false)
    private String maintenanceUnit;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
