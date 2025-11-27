package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.WorkOrderDTO;
import com.schedule.elevator.entity.WorkOrder;
import com.schedule.elevator.service.IWorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/work-order")
public class WorkOrderController {

    @Autowired
    private IWorkOrderService workOrderService;

    @PostMapping("/add")
    public BaseResponse create(@RequestBody WorkOrder workOrder) {
        workOrderService.save(workOrder);
        return new BaseResponse(HttpStatus.OK.value(), "添加成功", workOrder, null);
    }

    @GetMapping("/{id}")
    public BaseResponse getById(@PathVariable Long id) {
        WorkOrder workOrder = workOrderService.getById(id);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", workOrder, null);
    }

    @PutMapping("/update")
    public BaseResponse update(@RequestBody WorkOrder workOrder) {
        workOrderService.updateById(workOrder);
        return new BaseResponse(HttpStatus.OK.value(), "更新成功", workOrder, null);
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        workOrderService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "删除成功", null, null);
    }

    @GetMapping("/list")
    public BaseResponse list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute WorkOrderDTO workOrderDTO) {
        Page<WorkOrder> workOrderPage = workOrderService.queryByConditionsPage(workOrderDTO);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", workOrderPage, null);
    }
}