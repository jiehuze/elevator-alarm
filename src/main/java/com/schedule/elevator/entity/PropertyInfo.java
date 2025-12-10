package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物业信息实体类（含单位唯一标识 code）
 */
@Data
@TableName("property_info")
public class PropertyInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 使用单位唯一标识Code（如统一社会信用代码、系统分配编码等）
     */
    @TableField("unit_code")
    private String unitCode; //使用单位唯一标识Code

    @TableField("user_unit")
    private String userUnit; //使用单位

    @TableField("user_unit_manager")
    private String userUnitManager; //使用单位负责人

    @TableField("user_unit_manager_phone")
    private String userUnitManagerPhone; //使用单位负责人手机

    @TableField("safety_officer_name")
    private String safetyOfficerName; //物业安全负责人

    @TableField("safety_officer_phone")
    private String safetyOfficerPhone; //物业安全负责人手机

    @TableField("project_name")
    private String projectName; //项目名称

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
