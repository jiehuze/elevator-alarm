package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.ApplyApproveQueryDTO;
import com.schedule.elevator.entity.ApplyApprove;

/**
 * 统一审批服务接口
 */
public interface IApplyApproveService extends IService<ApplyApprove> {

    /**
     * 分页查询审批列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<ApplyApprove> queryPage(ApplyApproveQueryDTO queryDTO);

    /**
     * 提交申请
     *
     * @param applyApprove 申请信息
     * @return 申请单号
     */
    String submitApply(ApplyApprove applyApprove);

    /**
     * 审批通过
     *
     * @param id             申请ID
     * @param approveUserId  审批人ID
     * @param approveComment 审批意见
     * @return 是否成功
     */
    boolean approve(Long id, Integer approveUserId, String approveComment);

    /**
     * 审批拒绝
     *
     * @param id             申请ID
     * @param approveUserId  审批人ID
     * @param approveComment 审批意见
     * @return 是否成功
     */
    boolean reject(Long id, Integer approveUserId, String approveComment);

    /**
     * 根据申请单号查询
     *
     * @param applyNo 申请单号
     * @return 审批信息
     */
    ApplyApprove getByApplyNo(String applyNo);
}
