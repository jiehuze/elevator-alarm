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
 * 统一审批表
 */
@Data
@Accessors(chain = true)
@TableName("apply_approve")
public class ApplyApprove implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("apply_no")
    private String applyNo; // 申请单号

    @TableField("apply_type")
    private Integer applyType; // 申请类型 1员工变更2维保公司变更3注销脱保

    @TableField("apply_user_id")
    private Integer applyUserId; // 申请人ID

    @TableField("apply_user_name")
    private String applyUserName; // 申请人姓名

    @TableField("apply_data")
    private String applyData; // 申请内容JSON

    @TableField("status")
    private Integer status; // 审批状态 0待审批 1通过 2拒绝

    @TableField("approve_user_id")
    private Integer approveUserId; // 审批人ID

    @TableField("approve_user_name")
    private String approveUserName; // 审批人姓名

    @TableField("approve_comment")
    private String approveComment; // 审批意见

    @TableField("approve_time")
    private LocalDateTime approveTime; // 审批时间

    @TableField("created_at")
    private LocalDateTime createdAt; // 创建时间

    @TableField("updated_at")
    private LocalDateTime updatedAt; // 更新时间
}
