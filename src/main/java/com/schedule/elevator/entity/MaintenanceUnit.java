package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}