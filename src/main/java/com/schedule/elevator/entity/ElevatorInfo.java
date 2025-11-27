package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("elevator_info") // 对应你的表名
public class ElevatorInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String elevatorNo;        // 电梯编号
    private String location;          // 电梯位置
    private String organization;      // 所属单位或小区
    private String managerName;       // 电梯负责人
    private String managerTitle;      // 负责人所有职务
    private String description;       // 电梯描述
    private BigDecimal latitude;      // 纬度
    private BigDecimal longitude;     // 经度
    private String managerPhone;      // 电梯负责人电话
    private String backupContact;     // 电梯备用联系人
    private String backupPhone;       // 备用联系人电话

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}