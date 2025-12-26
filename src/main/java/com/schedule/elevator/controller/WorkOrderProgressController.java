package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.entity.WorkOrderProgress;
import com.schedule.elevator.service.IWorkOrderProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/work-order-progress")
public class WorkOrderProgressController {

    @Autowired
    private IWorkOrderProgressService progressService;

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

    @PutMapping
    public BaseResponse update(@RequestBody WorkOrderProgress progress) {
        progressService.updateById(progress);
        return new BaseResponse(HttpStatus.OK.value(), "更新成功", progress, null);
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