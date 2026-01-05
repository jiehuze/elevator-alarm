package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("work_order_progress")
public class WorkOrderProgress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo; // 工单编号

    @TableField("progress")
    private String progress; // 处理进度

    @TableField("result")
    private String result;  // 处理结果

    @TableField("status")
    private Integer status; // 转态和进度

    @TableField("remark")
    private String remark; // 处理备注

    @TableField("employee_id")
    private String employeeId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}