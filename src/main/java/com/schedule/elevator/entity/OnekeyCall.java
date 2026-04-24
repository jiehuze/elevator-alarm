package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 一键呼记录表
 */
@Data
@Accessors(chain = true)
@TableName(value = "onekey_call", autoResultMap = true)
public class OnekeyCall implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 呼叫设备码
     */
    @TableField("device_code")
    private String deviceCode;

    /**
     * 救援码
     */
    @TableField("rescue_code")
    private String rescueCode;

    /**
     * 区县
     */
    @TableField("district")
    private String district;

    /**
     * 项目名
     */
    @TableField("project_name")
    private String projectName;

    /**
     * 一键呼编号
     */
    @TableField("call_no")
    private String callNo;

    /**
     * 呼叫录音
     */
    @TableField("record_url")
    private String recordUrl;

    /**
     * 工单编号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 呼叫时间
     */
    @TableField("call_time")
    private LocalDateTime callTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

}
