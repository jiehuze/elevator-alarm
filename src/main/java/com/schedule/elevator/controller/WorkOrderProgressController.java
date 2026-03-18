package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.entity.WorkOrder;
import com.schedule.elevator.entity.WorkOrderProgress;
import com.schedule.elevator.enums.WorkOrderStatusEnum;
import com.schedule.elevator.service.IWorkOrderProgressService;
import com.schedule.elevator.service.IWorkOrderService;
import com.schedule.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/work-order-progress")
public class WorkOrderProgressController {

    @Autowired
    private IWorkOrderProgressService progressService;

    @Autowired
    private IWorkOrderService workOrderService;

    @PostMapping
    public BaseResponse create(@RequestBody WorkOrderProgress progress) {
        progressService.save(progress);
        return new BaseResponse(HttpStatus.OK.value(), "添加成功", progress, null);
    }

    @GetMapping("/{id}")
    public BaseResponse getById(@PathVariable Long id) {
        WorkOrderProgress wo = progressService.getById(id);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", wo, null);
    }

    @PutMapping("/update")
    public BaseResponse update(@RequestBody WorkOrderProgress progress) {
        progressService.update(progress, new LambdaQueryWrapper<WorkOrderProgress>().eq(WorkOrderProgress::getId, progress.getId()));
        return new BaseResponse(HttpStatus.OK.value(), "更新成功", progress, null);
    }

    @PutMapping("/update-batch")
    public BaseResponse updateBatch(@RequestBody List<WorkOrderProgress> progressList) {
        if (progressList == null || progressList.size() <= 0) {
            return new BaseResponse(HttpStatus.NO_CONTENT.value(), "无更新内容", null, null);
        }
        String orderNo = progressList.get(0).getOrderNo();
        WorkOrder workOrder = new WorkOrder().setOrderNo(orderNo);

        LocalDateTime dispatchTime = null, rescueTime = null, repairTime = null, arriveTime = null;

        for (WorkOrderProgress progress : progressList) {
            progressService.update(progress, new LambdaQueryWrapper<WorkOrderProgress>().eq(WorkOrderProgress::getId, progress.getId()));

            //派单时间，计算用时
            if (progress.getStatus().equals(WorkOrderStatusEnum.CREATED.getCode()) && progress.getIsSuccess() == 1) {
                workOrder.setCreateTime(progress.getCreateTime());
            }
            if (progress.getStatus().equals(WorkOrderStatusEnum.DISPATCHED.getCode()) && progress.getIsSuccess() == 1) {
                dispatchTime = progress.getCreateTime();
            }
            if (progress.getStatus().equals(WorkOrderStatusEnum.RESCUE_ARRIVED.getCode()) && progress.getIsSuccess() == 1) {
                arriveTime = progress.getCreateTime();
            }
            //救援完成时间，计算用时
            if (progress.getStatus().equals(WorkOrderStatusEnum.RESCUE_COMPLETED.getCode()) && progress.getIsSuccess() == 1) {
                rescueTime = progress.getCreateTime();
            }
            //维修完成时间，计算用时
            if (progress.getStatus().equals(WorkOrderStatusEnum.MAINTENANCE_COMPLETED.getCode()) && progress.getIsSuccess() == 1) {
                repairTime = progress.getCreateTime();
            }
        }
        if (dispatchTime != null && arriveTime != null) {
            workOrder.setTimeToArrive(DateUtils.calculateTimeDifferenceInSeconds(dispatchTime, arriveTime));
        }
        if (rescueTime != null && arriveTime != null) {
            workOrder.setRescueDuration(DateUtils.calculateTimeDifferenceInSeconds(arriveTime, rescueTime));
        }
        if (repairTime != null && arriveTime != null) {
            workOrder.setRepairDuration(DateUtils.calculateTimeDifferenceInSeconds(arriveTime, repairTime));
        }

        workOrderService.update(workOrder, new LambdaQueryWrapper<WorkOrder>().eq(WorkOrder::getOrderNo, orderNo));

        return new BaseResponse(HttpStatus.OK.value(), "更新成功", progressList, null);
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        progressService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "删除成功", null, null);
    }

    @GetMapping("/list")
    public BaseResponse listByOrderNo(
            @RequestParam String orderNo,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        Page<WorkOrderProgress> page = new Page<>(current, size);
        Page<WorkOrderProgress> workOrderProgresses = progressService.lambdaQuery()
                .eq(WorkOrderProgress::getOrderNo, orderNo)
                .page(page);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", workOrderProgresses, null);
    }
}