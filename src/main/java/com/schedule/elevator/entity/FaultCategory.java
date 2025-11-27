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
    private String name;
    private Long parentId;
    private Byte level;
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private List<FaultCategory> children;
}