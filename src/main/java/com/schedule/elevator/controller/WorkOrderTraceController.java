package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.entity.WorkOrderTrace;
import com.schedule.elevator.service.IWorkOrderTraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/work-order-trace")
public class WorkOrderTraceController {

    @Autowired
    private IWorkOrderTraceService traceService;

    @PostMapping
    public BaseResponse create(@RequestBody WorkOrderTrace trace) {
        traceService.save(trace);
        return new BaseResponse(HttpStatus.OK.value(), "添加成功", trace, null);
    }

    @GetMapping("/{id}")
    public BaseResponse getById(@PathVariable Long id) {
        WorkOrderTrace wot = traceService.getById(id);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", wot, null);
    }

    @PutMapping
    public BaseResponse update(@RequestBody WorkOrderTrace trace) {
        boolean update = traceService.updateById(trace);
        return new BaseResponse(update ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value(),
                update ? "更新成功" : "更新失败", trace, null);
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        traceService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "删除成功", null, null);
    }

    @GetMapping
    public BaseResponse listByOrderNo(
            @RequestParam String orderNo,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        Page<WorkOrderTrace> page = new Page<>(current, size);
        Page<WorkOrderTrace> workOrderTracePage = traceService.lambdaQuery()
                .eq(WorkOrderTrace::getOrderNo, orderNo)
                .orderByAsc(WorkOrderTrace::getHandledAt)
                .page(page);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", workOrderTracePage, null);
    }
}