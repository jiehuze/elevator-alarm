package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 救援单位 信息
 */
@Data
@TableName("maintenance")
public class Maintenance implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("site_name")
    private String siteName;

    @TableField("maintenance_company")
    private String maintenanceCompany;

    @TableField("maintainer_name")
    private String maintainerName;

    @TableField("company_phone")
    private String companyPhone;

    @TableField("maintainer_phone")
    private String maintainerPhone;

    @TableField("company_address")
    private String companyAddress;

    // 纬度：-90.00000000 ~ +90.00000000
    @TableField("latitude")
    private BigDecimal latitude;

    // 经度：-180.00000000 ~ +180.00000000
    @TableField("longitude")
    private BigDecimal longitude;

    @TableField("company_code")
    private String companyCode;

    @TableField("company_manager")
    private String companyManager;

    @TableField("company_level")
    private Integer companyLevel; // 1:初级, 2:中级, 3:高级

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}