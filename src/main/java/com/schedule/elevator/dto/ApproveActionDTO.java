package com.schedule.elevator.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 审批操作DTO
 */
@Data
public class ApproveActionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID
     */
    private Long id;

    /**
     * 审批人ID
     */
    private Integer approveUserId;

    /**
     * 审批意见
     */
    private String approveComment;
}
