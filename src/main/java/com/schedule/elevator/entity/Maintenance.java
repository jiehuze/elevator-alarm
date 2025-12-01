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
    private String siteName; // 站点名称

    @TableField("maintenance_company")
    private String maintenanceCompany; // 维护公司名称

    @TableField("maintainer_name")
    private String maintainerName; // 维护单位名称

    @TableField("company_phone")
    private String companyPhone; // 维护公司电话

    @TableField("maintainer_phone")
    private String maintainerPhone; // 维护人电话

    @TableField("province")
    private String province; // 省份
    @TableField("city")
    private String city; // 市
    @TableField("district")
    private String district; // 区

    @TableField("company_address")
    private String companyAddress; // 维护公司地址

    // 纬度：-90.00000000 ~ +90.00000000
    @TableField("latitude")
    private BigDecimal latitude; // 维护公司纬度

    // 经度：-180.00000000 ~ +180.00000000
    @TableField("longitude")
    private BigDecimal longitude; //  维护公司经度

    @TableField("company_code")
    private String companyCode; // 维护公司编号

    @TableField("company_manager")
    private String companyManager; // 联系人

    @TableField("company_level")
    private Integer companyLevel; // 1:初级, 2:中级, 3:高级

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}