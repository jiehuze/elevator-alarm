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

    @TableField("maintainer_unit_name")
    private String maintainerUnitName; // 维护单位名称

    @TableField("maintainer_unit_manager")
    private String maintainerUnitManager; // 维保单位负责人

    @TableField("maintainer_unit_manager_phone")
    private String maintainerUnitManagerPhone; // 维保单位负责人电话

    @TableField("maintainer_unit_address")
    private String companyAddress; // 维护公司地址

    @TableField("maintainer_unit_type")
    private String maintainerUnitType; // 维保单位类型

    @TableField("maintainer_unit_status")
    private String maintainerUnitStatus; // 维保单位状态

    @TableField("maintainer_unit_code")
    private String maintainerUnitCode; // 维保单位编码（营业执照）

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