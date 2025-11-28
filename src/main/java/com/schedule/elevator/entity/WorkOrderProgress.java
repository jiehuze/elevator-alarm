package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
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
    private Byte status; // 转态和进度

    @TableField("remark")
    private String remark; // 处理备注

    @TableField("fault_content")
    private String faultContent; // 故障内容

    @TableField("fault_content_id")
    private Long faultContentId;     // 故障内容ID

    // 虽然 DB 有默认值，但仍建议 MP 填充以保证一致性
    private LocalDateTime createTime;

    @TableField("employee_id")
    private String employeeId;

    private LocalDateTime updateTime;
}