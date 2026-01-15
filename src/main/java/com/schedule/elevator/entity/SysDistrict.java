package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("sys_district")
public class SysDistrict implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("district_code")
    private String districtCode;  // 区域编码

    @TableField("district_name")
    private String districtName;  // 区域名称

    @TableField("parent_code")
    private String parentCode;    // 上级区域编码

    @TableField("district_level")
    private Integer districtLevel; // 区域级别

    @TableField("sort")
    private Integer sort;         // 排序号

    @TableField("is_enabled")
    private Boolean enabled;      // 是否启用

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("remark")
    private String remark;        // 备注
}
