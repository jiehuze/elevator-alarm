package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("safety_officer")
public class SafetyOfficer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("safety_officer_name")
    private String safetyOfficerName;

    @TableField("safety_officer_phone")
    private String safetyOfficerPhone;

    @TableField("using_unit_id")
    private Long usingUnitId;

    @TableField("using_unit")
    private String usingUnit;

    @TableField("status")
    private Integer status = 1; // 默认在职

    @TableField(exist = false)
    private Long count;  // 计数属性，不映射到数据库

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
