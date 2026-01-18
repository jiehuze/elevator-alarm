package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 维保单位信息
 */
@Data
@TableName("maintenance_unit")
public class MaintenanceUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id; // 维保单位ID

    @TableField("maintenance_unit")
    private String maintenanceUnit; // 维护单位名称

    @TableField("maintenance_unit_phone")
    private String maintenanceUnitPhone; // 维保单位电话

    @TableField("maintenance_unit_manager")
    private String maintenanceUnitManager; // 维保单位负责人

    @TableField("maintenance_unit_manager_phone")
    private String maintenanceUnitManagerPhone; // 维保单位负责人电话

    @TableField("maintenance_unit_address")
    private String companyAddress; // 维护公司地址

    @TableField("maintenance_unit_type")
    private String maintenanceUnitType; // 维保单位类型

    @TableField("maintenance_unit_status")
    private String maintenanceUnitStatus; // 维保单位状态

    @TableField("maintenance_unit_code")
    private String maintenanceUnitCode; // 维保单位编码（营业执照）

    @TableField("level")
    private Integer level; // 维保单位级别,1：一级维保单位，2：二级维保单位

    @TableField("address")
    private String address; // 完整小区地址（原始文本）

    @TableField(exist = false)
    private Long count;  // 计数属性，不映射到数据库

    @TableField(exist = false)
    private List<MaintenanceTeam> teams;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}