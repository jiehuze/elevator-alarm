package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.ApproveActionDTO;
import com.schedule.elevator.dto.ApplyApproveQueryDTO;
import com.schedule.elevator.entity.ApplyApprove;
import com.schedule.elevator.service.IApplyApproveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一审批控制器
 */
@RestController
@RequestMapping("/check")
public class ApplyApproveController {

    @Autowired
    private IApplyApproveService applyApproveService;

    /**
     * 分页查询审批列表
     */
    @GetMapping("/list")
    public BaseResponse queryPage(@ModelAttribute ApplyApproveQueryDTO queryDTO) {
        Page<ApplyApprove> page = applyApproveService.queryPage(queryDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", page, null);
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        boolean success = applyApproveService.removeById(id);
        if (success) {
            return new BaseResponse(HttpStatus.OK.value(), "删除成功", null, null);
        } else {
            return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "删除失败", null, null);
        }
    }

    /**
     * 根据申请单号查询
     */
    @GetMapping("/detail/{applyNo}")
    public BaseResponse getByApplyNo(@PathVariable String applyNo) {
        ApplyApprove applyApprove = applyApproveService.getByApplyNo(applyNo);
        if (applyApprove == null) {
            return new BaseResponse(HttpStatus.NOT_FOUND.value(), "申请单不存在", null, null);
        }
        return new BaseResponse(HttpStatus.OK.value(), "success", applyApprove, null);
    }

    /**
     * 提交申请
     */
    @PostMapping("/submit")
    public BaseResponse submitApply(@RequestBody ApplyApprove applyApprove) {
        try {
            String applyNo = applyApproveService.submitApply(applyApprove);
            Map<String, String> result = new HashMap<>();
            result.put("applyNo", applyNo);
            return new BaseResponse(HttpStatus.OK.value(), "提交成功", result, null);
        } catch (Exception e) {
            return new BaseResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null, null);
        }
    }

    /**
     * 审批通过
     */
    @PostMapping("/approve")
    public BaseResponse approve(@RequestBody ApproveActionDTO actionDTO) {
        try {
            boolean success = applyApproveService.approve(actionDTO.getId(), actionDTO.getApproveUserId(), actionDTO.getApproveComment());
            if (success) {
                return new BaseResponse(HttpStatus.OK.value(), "审批通过", null, null);
            } else {
                return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "审批失败", null, null);
            }
        } catch (IllegalArgumentException e) {
            return new BaseResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null, null);
        } catch (Exception e) {
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统错误：" + e.getMessage(), null, null);
        }
    }

    /**
     * 审批拒绝
     */
    @PostMapping("/reject")
    public BaseResponse reject(@RequestBody ApproveActionDTO actionDTO) {
        try {
            boolean success = applyApproveService.reject(actionDTO.getId(), actionDTO.getApproveUserId(), actionDTO.getApproveComment());
            if (success) {
                return new BaseResponse(HttpStatus.OK.value(), "审批已拒绝", null, null);
            } else {
                return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "审批失败", null, null);
            }
        } catch (IllegalArgumentException e) {
            return new BaseResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null, null);
        } catch (Exception e) {
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统错误：" + e.getMessage(), null, null);
        }
    }

    /**
     * 获取申请类型枚举
     */
    @GetMapping("/apply-types")
    public BaseResponse getApplyTypes() {
        Map<Integer, String> types = new HashMap<>();
        types.put(1, "员工变更");
        types.put(2, "维保公司变更");
        types.put(3, "注销脱保");
        return new BaseResponse(HttpStatus.OK.value(), "success", types, null);
    }

    /**
     * 获取审批状态枚举
     */
    @GetMapping("/status-list")
    public BaseResponse getStatusList() {
        Map<Integer, String> status = new HashMap<>();
        status.put(0, "待审批");
        status.put(1, "已通过");
        status.put(2, "已拒绝");
        return new BaseResponse(HttpStatus.OK.value(), "success", status, null);
    }
}
