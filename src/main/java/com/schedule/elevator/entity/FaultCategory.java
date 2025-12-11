package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("fault_category")
public class FaultCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 层级：1级，2级
     */
    private Byte level;
    /**
     * 具体故障代码（如 E101）
     */
    private String faultCode;

    /**
     * 故障分析（详细说明）
     */
    private String faultAnalysis;

    /**
     * 上一层ID
     */
    private Long parentId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private List<FaultCategory> children;
}