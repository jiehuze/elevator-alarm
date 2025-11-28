package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("work_order_trace")
public class WorkOrderTrace implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("employee_id")
    private String employeeId;

    @TableField("handled_at")
    private LocalDateTime handledAt;

    @TableField("description")
    private String description;

    private LocalDateTime createdAt;
}