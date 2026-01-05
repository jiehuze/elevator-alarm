package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("fault_records")
public class FaultRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo; // 工单编号

    @TableField("root_code")
    private String rootCode; // 一级故障码（根故障码）

    @TableField("sub_code")
    private String subCode; // 二级故障码（子故障码）

    private LocalDateTime createdAt; // 创建时间
}
