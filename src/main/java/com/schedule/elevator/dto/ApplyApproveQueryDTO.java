package com.schedule.elevator.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审批查询DTO
 */
@Data
public class ApplyApproveQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请单号
     */
    private String applyNo;

    /**
     * 申请类型 1员工变更2维保公司变更3注销脱保
     */
    private Integer applyType;

    /**
     * 申请类型 1员工变更2维保公司变更3注销脱保,使用逗号分割
     */
    private String applyTypes;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 审批状态 0待审批 1通过 2拒绝
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 当前页
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
