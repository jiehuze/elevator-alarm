package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @TableField("level")
    private Integer level; // 维保班组级别 1=一级，2=二级

    @TableField("province")
    private String province; // 省份

    @TableField("city")
    private String city; // 市

    @TableField("district")
    private String district; // 区

    // 纬度：-90.00000000 ~ +90.00000000
    @TableField("latitude")
    private BigDecimal latitude; // 维护公司纬度

    // 经度：-180.00000000 ~ +180.00000000
    @TableField("longitude")
    private BigDecimal longitude; //  维护公司经度

    @TableField("address")
    private String address; // 完整小区地址（原始文本）

    @TableField("leader_name")
    private String leaderName; // 班组负责人

    @TableField("leader_phone")
    private String leaderPhone; // 班组负责人电话

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private Long numbers;  // 计数属性，人数

    @TableField(exist = false)
    private Long count;  // 计数属性，不映射到数据库

    @TableField(exist = false)
    private List<MaintenancePersonnel> persons;
}
