package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 维保班组信息
 */
@Data
@TableName("maintenance_team")
public class MaintenanceTeam {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("maintenance_unit_id")
    private Long maintenanceUnitId; // 维保单位ID

    @TableField("team_name")
    private String teamName; // 班组名称

    @TableField("leader_name")
    private String leaderName; // 班组负责人

    @TableField("leader_phone")
    private String leaderPhone; // 班组负责人电话

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
