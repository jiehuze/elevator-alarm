package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.HandleDTO;
import com.schedule.elevator.dto.WorkOrderDTO;
import com.schedule.elevator.entity.PropertyInfo;
import com.schedule.elevator.entity.WorkOrder;
import com.schedule.elevator.entity.WorkOrderProgress;
import com.schedule.elevator.service.IPropertyInfoService;
import com.schedule.elevator.service.IWorkOrderProgressService;
import com.schedule.elevator.service.IWorkOrderService;
import com.schedule.elevator.service.IWorkOrderTraceService;
import com.schedule.utils.DateUtils;
import com.schedule.utils.WordFileReplace;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private IPropertyInfoService propertyInfoService;

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

    @GetMapping("/export/{id}")
    public void exportReport(@PathVariable Long id, HttpServletResponse response) {
        WorkOrder workOrder = workOrderService.getById(id);
        if (workOrder == null) {
            return;
        }

        PropertyInfo propertyInfo = propertyInfoService.getById(workOrder.getUsingUnitId());
        List<WorkOrderProgress> wps = workOrderProgressService.queryByOrderNo(workOrder.getOrderNo());
        HashMap<Byte, WorkOrderProgress> wpMap = new HashMap<>();
        for (WorkOrderProgress wp : wps) {
            wpMap.put(wp.getStatus(), wp);
        }

        Map<String, String> replacements = Map.ofEntries(
                new SimpleEntry<>("{{orderName}}", workOrder.getOrderNo()), // todo 工单名称
                new SimpleEntry<>("{{orderNo}}", workOrder.getOrderNo()),
                new SimpleEntry<>("{{createTime}}", DateUtils.format(workOrder.getCreateTime(), DateUtils.DATE_TIME_PATTERN_CHINA)),
                new SimpleEntry<>("{{registerCode}}", workOrder.getRegisterCode()),
                new SimpleEntry<>("{{rescueCode}}", workOrder.getRescueCode()),
                new SimpleEntry<>("{{usingUnit}}", workOrder.getUsingUnit()),
                new SimpleEntry<>("{{usingUnitPhone}}", propertyInfo.getUsingUnitManagerPhone()),
                new SimpleEntry<>("{{maintenanceUnitName}}", workOrder.getMaintenanceUnitName()),
                new SimpleEntry<>("{{maintenanceUnitPhone}}", workOrder.getMaintenancePersonnelPhone()),
                new SimpleEntry<>("{{elevatorAddress}}", workOrder.getElevatorAddress()),
                new SimpleEntry<>("{{alarmPersonName}}", workOrder.getAlarmPersonName()),
                new SimpleEntry<>("{{alarmPersonPhone}}", workOrder.getAlarmPersonPhone()),
                new SimpleEntry<>("{{incidentDescription}}", workOrder.getIncidentDescription()),
                new SimpleEntry<>("{{trappedCount}}", workOrder.getTrappedCount().toString()),
                new SimpleEntry<>("{{injuredCount}}", workOrder.getInjuredCount().toString()),
                new SimpleEntry<>("{{suspectedDeathCount}}", workOrder.getSuspectedDeathCount().toString()),
                new SimpleEntry<>("{{alarmTime}}", DateUtils.format(workOrder.getAlarmTime(), DateUtils.DATE_TIME_PATTERN_CHINA)),
                new SimpleEntry<>("{{arrivalTime}}", DateUtils.format(wpMap.get(Byte.valueOf("4")).getCreateTime(), DateUtils.DATE_TIME_PATTERN_CHINA)), //todo 到达现场时间
                new SimpleEntry<>("{{completeTime}}", DateUtils.format(wpMap.get(Byte.valueOf("99")).getCreateTime(), DateUtils.DATE_TIME_PATTERN_CHINA)), //todo 救援完成时间
                new SimpleEntry<>("{{major}}", workOrder.getMajorIncident() ? "是" : "否"),
                new SimpleEntry<>("{{rescue}}", workOrder.getMedicalRescueStarted() ? "是" : "否")
        );

        String outputPath = "/tmp/report_" + System.currentTimeMillis() + ".docx";

        try {
            WordFileReplace.replaceTextInWordX(replacements, outputPath);

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", "attachment; filename=处置报告.docx");
            response.setHeader("Content-Length", String.valueOf(new File(outputPath).length()));

            // 将文件写入响应流
            try (FileInputStream fis = new FileInputStream(outputPath);
                 OutputStream os = response.getOutputStream()) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            } finally {
                // 删除临时文件
                File tempFile = new File(outputPath);
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            e.printStackTrace();
        }
    }
}