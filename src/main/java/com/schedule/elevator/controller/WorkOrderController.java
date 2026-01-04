package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.HandleDTO;
import com.schedule.elevator.dto.WorkOrderDTO;
import com.schedule.elevator.entity.WorkOrder;
import com.schedule.elevator.entity.WorkOrderProgress;
import com.schedule.elevator.service.IWorkOrderProgressService;
import com.schedule.elevator.service.IWorkOrderService;
import com.schedule.elevator.service.IWorkOrderTraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/work-order")
public class WorkOrderController {

    @Autowired
    private IWorkOrderService workOrderService;

    @Autowired
    private IWorkOrderTraceService workOrderTraceService;

    @Autowired
    private IWorkOrderProgressService workOrderProgressService;

    @Autowired
    private IWorkOrderProgressService progressService;

    /**
     * 创建工单
     *
     * @param workOrder
     * @return
     */
    @PostMapping("/create")
    public BaseResponse create(@RequestBody WorkOrder workOrder) {
        workOrder.setOrderNo(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        try {
            WorkOrder wd = workOrderService.createWorkOrder(workOrder);
            //添加记录
            WorkOrderProgress workOrderProgress = new WorkOrderProgress().setOrderNo(workOrder.getOrderNo())
                    .setProgress("创建工单成功")
                    .setResult("创建工单成功")
                    .setStatus(workOrder.getStatus())
                    .setRemark(workOrder.getIncidentDescription())
                    .setEmployeeId(workOrder.getEmployeeId());
            workOrderProgressService.save(workOrderProgress);

            return new BaseResponse(HttpStatus.OK.value(), "添加成功", wd, null);
        } catch (Exception e) {
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "添加失败", e.getMessage(), null);
        }
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
            //添加记
            WorkOrderProgress workOrderProgress = new WorkOrderProgress().setOrderNo(workOrder.getOrderNo())
                    .setProgress("派单")
                    .setResult("派单成功")
                    .setStatus(workOrder.getStatus())
                    .setEmployeeId(workOrder.getEmployeeId());
            workOrderProgressService.save(workOrderProgress);
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
    public BaseResponse HandleWorkOrder(@RequestBody HandleDTO handleDTO) {
        System.out.println("1---------" + handleDTO);
//        System.out.println("2----------" + workOrder1);
        try {
            WorkOrder workOrder = new WorkOrder();
            workOrder.setOrderNo(handleDTO.getOrderNo())
                    .setOrderType(handleDTO.getOrderType())
                    .setInjuredCount(handleDTO.getInjuredCount())
                    .setTrappedCount(handleDTO.getTrappedCount())
                    .setSuspectedDeathCount(handleDTO.getSuspectedDeathCount())
                    .setStatus(handleDTO.getStatus());

            WorkOrderProgress workOrderProgress = new WorkOrderProgress().setOrderNo(handleDTO.getOrderNo())
                    .setProgress(handleDTO.getProgress())
                    .setResult(handleDTO.getResult())
                    .setStatus(handleDTO.getStatus())
                    .setEmployeeId(handleDTO.getEmployeeId())
//                    .setFaultContent(handleDTO.getFaultContent())
//                    .setFaultContentId(handleDTO.getFaultContentId())
                    .setRemark(handleDTO.getRemark());
            boolean save = workOrderProgressService.save(workOrderProgress);
            Boolean update = workOrderService.updateByOrderNo(workOrder);

            return new BaseResponse(HttpStatus.OK.value(), "更新成功", update, null);
        } catch (Exception e) {
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "更新失败", e.getMessage(), null);
        }
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
        boolean update = workOrderService.update(workOrder, new LambdaQueryWrapper<WorkOrder>().eq(WorkOrder::getId, workOrder.getId()));
        return new BaseResponse(HttpStatus.OK.value(), "更新成功", update, null);
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        workOrderService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "删除成功", null, null);
    }

    @GetMapping("/list")
    public BaseResponse list(@ModelAttribute WorkOrderDTO workOrderDTO) {
        System.out.println("---------------" + workOrderDTO);
        Page<WorkOrder> workOrderPage = workOrderService.queryByConditionsPage(workOrderDTO);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", workOrderPage, null);
    }

    @GetMapping("/progress")
    public BaseResponse listProgress(@RequestParam String orderNo) {
        List<WorkOrderProgress> list = progressService.lambdaQuery()
                .eq(WorkOrderProgress::getOrderNo, orderNo)
                .orderByAsc(WorkOrderProgress::getUpdateTime)
                .list();

        return new BaseResponse(HttpStatus.OK.value(), "查询成功", list, null);
    }

}