package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.WorkOrderDTO;
import com.schedule.elevator.entity.WorkOrder;
import com.schedule.elevator.entity.WorkOrderProgress;
import com.schedule.elevator.entity.WorkOrderTrace;
import com.schedule.elevator.service.IWorkOrderProgressService;
import com.schedule.elevator.service.IWorkOrderService;
import com.schedule.elevator.service.IWorkOrderTraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/work-order")
public class WorkOrderController {

    @Autowired
    private IWorkOrderService workOrderService;

    @Autowired
    private IWorkOrderTraceService workOrderTraceService;

    @Autowired
    private IWorkOrderProgressService workOrderProgressService;

    /**
     * 创建工单
     *
     * @param workOrder
     * @return
     */
    @PostMapping("/create")
    public BaseResponse create(@RequestBody WorkOrder workOrder) {
        workOrder.setStatus(Byte.valueOf("1"));
        workOrder.setOrderNo(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        boolean insert = workOrderService.save(workOrder);

        if (!insert) {
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "添加失败", null, null);
        } else {
            //添加记录
            WorkOrderTrace workOrderTrace = new WorkOrderTrace();
            workOrderTrace.setOrderNo(workOrder.getOrderNo())
                    .setEmployeeId(workOrder.getEmployeeId())
                    .setDescription("创建了" + workOrder.getOrderType() + "工单");
            workOrderTraceService.save(workOrderTrace);
        }

        return new BaseResponse(HttpStatus.OK.value(), "添加成功", workOrder, null);
    }

    /**
     * 创建救援信息和对应的级别
     *
     * @return
     */
    @PostMapping("/create-rescue")
    public BaseResponse createRescue(@RequestBody WorkOrder workOrder) {
        Boolean res = workOrderService.createRescueInfo(workOrder);
        if (!res) {
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "创建失败", null, null);
        } else {
            //添加记录
            WorkOrderTrace workOrderTrace = new WorkOrderTrace();
            workOrderTrace.setOrderNo(workOrder.getOrderNo())
                    .setEmployeeId(workOrder.getEmployeeId())
                    .setDescription("选择了" + workOrder.getRescueLevel() + "派单成功");
            workOrderTraceService.save(workOrderTrace);
        }
        return new BaseResponse(HttpStatus.OK.value(), "创建救援信息成功", res, null);
    }

    /**
     * 处理工单，更新进度
     *
     * @param
     * @return
     */
    @PutMapping("/handle")
    public BaseResponse HandleWorkOrder(@RequestBody WorkOrderProgress workOrderProgress) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setOrderNo(workOrderProgress.getOrderNo()).setStatus(workOrderProgress.getStatus());

        boolean save = workOrderProgressService.save(workOrderProgress);
        workOrderService.updateStatus(workOrder);

        return new BaseResponse(HttpStatus.OK.value(), "更新成功", save, null);
    }

    /**
     * 设置重大事项
     *
     * @param workOrder
     * @return
     */
    @PutMapping("/major_incident")
    public BaseResponse setMajorIncident(@ModelAttribute WorkOrder workOrder) {
        Boolean update = workOrderService.updateByOrderNo(workOrder);
        if (!update) {
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "更新失败", null, null);
        }
        return new BaseResponse(HttpStatus.OK.value(), "设置重大事项成功", workOrder, null);
    }

    @PutMapping("/update-status")
    public BaseResponse updateStatus(@ModelAttribute WorkOrder workOrder) {
        workOrder.setStatus(workOrder.getStatus());
        workOrderService.updateById(workOrder);
        return new BaseResponse(HttpStatus.OK.value(), "更新成功", workOrder, null);
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