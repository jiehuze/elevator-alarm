package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.HandleDTO;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.*;
import com.schedule.elevator.enums.RescueLevelEnum;
import com.schedule.elevator.enums.WorkOrderStatusEnum;
import com.schedule.elevator.enums.WorkOrderTypeEnum;
import com.schedule.elevator.service.*;
import com.schedule.excel.WorkOrderExcel;
import com.schedule.excel.WorkOrderExcelConverter;
import com.schedule.utils.DateUtils;
import com.schedule.utils.ExcelUtil;
import com.schedule.utils.FileReplace;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import static com.schedule.utils.ExcelUtil.isExcelFile;

@RestController
@RequestMapping("/work-order")
public class WorkOrderController {

    @Autowired
    private IWorkOrderService workOrderService;

    @Autowired
    private IElevatorInfoService elevatorInfoService;

    @Autowired
    private IWorkOrderProgressService workOrderProgressService;

    @Autowired
    private IWorkOrderProgressService progressService;

    @Autowired
    private IPropertyInfoService propertyInfoService;

    @Autowired
    private IFaultCategoryService faultCategoryService;

    @Autowired
    private IFaultRecordService faultRecordService;

    @Autowired
    private ICommunityService communityService;

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
                    .setProgress(WorkOrderStatusEnum.getByCode(workOrder.getStatus()).getDescription())
                    .setResult("成功")
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
                    .setResult("成功")
                    .setStatus(workOrder.getStatus())
                    .setEmployeeId(workOrder.getEmployeeId());

            if (workOrder.getRescueLevel() != null) {
                if (workOrder.getRescueLevel().equals(RescueLevelEnum.LEVEL_1.getCode())
                        || workOrder.getRescueLevel().equals(RescueLevelEnum.LEVEL_2.getCode())) {
                    String progress = "选择" + RescueLevelEnum.getByCode(workOrder.getRescueLevel()).getDescription() + "派单";
                    workOrderProgress.setProgress(progress);
                } else if (workOrder.getRescueLevel().equals(RescueLevelEnum.LEVEL_3.getCode())) {
                    String progress = "选择" + RescueLevelEnum.getByCode(workOrder.getRescueLevel()).getDescription() + "派单";
                    workOrderProgress.setProgress(progress);
                    workOrderProgress.setRemark("救援电话：" + workOrder.getRescueHotline());
                }
            }

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
        try {
            WorkOrder workOrder = new WorkOrder();
            workOrder.setOrderNo(handleDTO.getOrderNo())
                    .setOrderType(handleDTO.getOrderType())
                    .setInjuredCount(handleDTO.getInjuredCount())
                    .setTrappedCount(handleDTO.getTrappedCount())
                    .setSuspectedDeathCount(handleDTO.getSuspectedDeathCount())
                    .setStatus(handleDTO.getStatus());
            HashMap<Integer, WorkOrderProgress> wMap = workOrderProgressService.queryMapByOrderNo(handleDTO.getOrderNo());

            //到达现场时间，计算用时
            if (handleDTO.getStatus().equals(WorkOrderStatusEnum.RESCUE_ARRIVED.getCode())) {
                WorkOrderProgress progress = wMap.get(WorkOrderStatusEnum.DISPATCHED.getCode());
                if (progress != null) {
                    workOrder.setTimeToArrive(DateUtils.calculateTimeDifferenceInSeconds(progress.getCreateTime(), LocalDateTime.now()));
                }
            }
            //救援完成时间，计算用时
            if (handleDTO.getStatus().equals(WorkOrderStatusEnum.RESCUE_COMPLETED.getCode())) {
                WorkOrderProgress progress = wMap.get(WorkOrderStatusEnum.RESCUE_ARRIVED.getCode());
                if (progress != null) {
                    workOrder.setRescueDuration(DateUtils.calculateTimeDifferenceInSeconds(progress.getCreateTime(), LocalDateTime.now()));
                }
            }
            //维修完成时间，计算用时
            if (handleDTO.getStatus().equals(WorkOrderStatusEnum.MAINTENANCE_COMPLETED.getCode())) {
                WorkOrderProgress progress = wMap.get(WorkOrderStatusEnum.RESCUE_ARRIVED.getCode());
                if (progress != null) {
                    workOrder.setRepairDuration(DateUtils.calculateTimeDifferenceInSeconds(progress.getCreateTime(), LocalDateTime.now()));

                }
            }

            WorkOrderProgress workOrderProgress = new WorkOrderProgress().setOrderNo(handleDTO.getOrderNo())
                    .setProgress(handleDTO.getProgress())
                    .setResult(handleDTO.getResult())
                    .setStatus(handleDTO.getStatus())
                    .setEmployeeId(handleDTO.getEmployeeId())
                    .setRemark(handleDTO.getRemark());
            workOrderProgressService.save(workOrderProgress);

            if (handleDTO.getFaults() != null && handleDTO.getFaults().size() > 0) {
                Map<String, FaultCategory> faultCategoryMap = faultCategoryService.getFaultCategoryMap();

                faultRecordService.saveBatch(handleDTO.getFaults());
                StringBuilder sb = new StringBuilder();
                for (FaultRecord faultRecord : handleDTO.getFaults()) {
                    if ("03".equals(faultRecord.getRootCode())
                            || "04".equals(faultRecord.getRootCode())
                            || "05".equals(faultRecord.getRootCode())
                            || "06".equals(faultRecord.getRootCode())
                            || "07".equals(faultRecord.getRootCode())
                            || "08".equals(faultRecord.getRootCode())
                            || "09".equals(faultRecord.getRootCode())) {
                        workOrder.setMechanicalFailure(true); // 机械故障
                    }
                    sb = sb.append(faultCategoryMap.get(faultRecord.getRootCode()).getFaultAnalysis()
                            + ":"
                            + faultCategoryMap.get(faultRecord.getSubCode()).getFaultAnalysis()
                            + "\n");
                }
                workOrder.setFailureReason(sb.toString());
            }

            workOrderService.updateByOrderNo(workOrder);

            return new BaseResponse(HttpStatus.OK.value(), "更新成功", true, null);
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
        HashMap<String, Object> map = new HashMap<>();
        WorkOrder workOrder = workOrderService.getById(id);
        System.out.println("workOrder:" + workOrder.getRescueCode());
        if (StringUtils.isNotBlank(workOrder.getRescueCode())) {
            ElevatorInfo elevatorInfo = elevatorInfoService.searchElevatorInfo(new SearchDTO().setRescueCode(workOrder.getRescueCode()));

            if (elevatorInfo != null && elevatorInfo.getCommunityId() != null) {
                Community community = communityService.getOne(new LambdaQueryWrapper<Community>().eq(Community::getId, elevatorInfo.getCommunityId()));
                workOrder.setElevatorAddress(community.getAddress());
                elevatorInfo.setLocation(community.getAddress());
            }

            map.put("elevator", elevatorInfo);
        }
        map.put("workOrder", workOrder);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", map, null);
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
    public BaseResponse list(@ModelAttribute SearchDTO workOrderDTO) {
        System.out.println("---------------" + workOrderDTO);
        Page<WorkOrder> workOrderPage = workOrderService.queryByConditionsPage(workOrderDTO);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", workOrderPage, null);
    }

    @GetMapping("/progress")
    public BaseResponse listProgress(@RequestParam String orderNo) {
        List<WorkOrderProgress> list = progressService.lambdaQuery()
                .eq(WorkOrderProgress::getOrderNo, orderNo)
                .orderByAsc(WorkOrderProgress::getCreateTime)
                .list();

        return new BaseResponse(HttpStatus.OK.value(), "查询成功", list, null);
    }

    @GetMapping("/sync")
    public BaseResponse sync() {
        SearchDTO searchDTO = new SearchDTO().setCurrent(4).setSize(1000);
        Page<WorkOrder> workOrderPage = workOrderService.queryByConditionsPage(searchDTO);
        for (WorkOrder workOrder : workOrderPage.getRecords()) {
            LocalDateTime dispatchTime = null, rescueTime = null, repairTime = null, arriveTime = null;
            List<WorkOrderProgress> progressList = workOrderProgressService.queryByOrderNo(workOrder.getOrderNo());
            for (WorkOrderProgress progress : progressList) {
                //派单时间，计算用时
                if (progress.getStatus().equals(WorkOrderStatusEnum.CREATED.getCode())) {
                    workOrder.setCreateTime(progress.getCreateTime());
                }
                if (progress.getStatus().equals(WorkOrderStatusEnum.DISPATCHED.getCode())) {
                    dispatchTime = progress.getCreateTime();
                }
                if (progress.getStatus().equals(WorkOrderStatusEnum.RESCUE_ARRIVED.getCode())) {
                    arriveTime = progress.getCreateTime();
                }
                //救援完成时间，计算用时
                if (progress.getStatus().equals(WorkOrderStatusEnum.RESCUE_COMPLETED.getCode())) {
                    rescueTime = progress.getCreateTime();
                }
                //维修完成时间，计算用时
                if (progress.getStatus().equals(WorkOrderStatusEnum.MAINTENANCE_COMPLETED.getCode())) {
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

            System.out.println("------------ arrivetime: " + workOrder.getTimeToArrive() + " repairTime: " + workOrder.getRepairDuration() + " rescueTime: " + workOrder.getRescueDuration());

            workOrderService.update(workOrder, new LambdaQueryWrapper<WorkOrder>().eq(WorkOrder::getId, workOrder.getId()));
        }

        return new BaseResponse(HttpStatus.OK.value(), "查询成功", 0, null);
    }

    @GetMapping("/export-workorder-list")
    public void exportWorkOrder(@ModelAttribute SearchDTO workOrderDTO, HttpServletResponse response) throws Exception {
        // 设置响应头
        String fileName = URLEncoder.encode("历史工单", StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        ArrayList<WorkOrderExcel> dtoList = new ArrayList<>();

        List<WorkOrder> workOrders = workOrderService.queryByConditions(workOrderDTO);
        for (WorkOrder workOrder : workOrders) {
            HashMap<Integer, WorkOrderProgress> progressHashMap = workOrderProgressService.queryMapByOrderNo(workOrder.getOrderNo());

            LocalDateTime dispatchTime = Optional.ofNullable(progressHashMap.get(WorkOrderStatusEnum.DISPATCHED.getCode()))
                    .map(WorkOrderProgress::getCreateTime).orElse(null);

            LocalDateTime arriveTime = Optional.ofNullable(progressHashMap.get(WorkOrderStatusEnum.RESCUE_ARRIVED.getCode()))
                    .map(WorkOrderProgress::getCreateTime).orElse(null);

            LocalDateTime rescueTime = Optional.ofNullable(progressHashMap.get(WorkOrderStatusEnum.RESCUE_COMPLETED.getCode()))
                    .map(WorkOrderProgress::getCreateTime).orElse(null);

            LocalDateTime followUpTime = Optional.ofNullable(progressHashMap.get(WorkOrderStatusEnum.RESCUE_FOLLOW_UP.getCode()))
                    .map(WorkOrderProgress::getCreateTime).orElse(null);

            LocalDateTime repairTime = Optional.ofNullable(progressHashMap.get(WorkOrderStatusEnum.MAINTENANCE_COMPLETED.getCode()))
                    .map(WorkOrderProgress::getCreateTime).orElse(null);

            LocalDateTime closeTime = Optional.ofNullable(progressHashMap.get(WorkOrderStatusEnum.CLOSED.getCode()))
                    .map(WorkOrderProgress::getCreateTime).orElse(null);

            dtoList.add(WorkOrderExcelConverter.toDto(workOrder, dispatchTime, arriveTime, rescueTime, followUpTime, repairTime, closeTime));

            dtoList.add(WorkOrderExcelConverter.toDto(workOrder, dispatchTime, arriveTime, rescueTime, followUpTime, repairTime, closeTime));
        }

        System.out.println("list size:" + dtoList.toString());

        // 写入 Excel
        ExcelUtil.exportExcelToTargetWithTemplate(response, fileName, "历史工单", dtoList, WorkOrderExcel.class, "doc/workorder.xlsx");
    }

    @PostMapping("/import")
    public BaseResponse importElevators(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 校验是否为 Excel 文件（可选）
            if (!isExcelFile(file)) {
                return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "请上传 .xls 或 .xlsx 文件", null, null);
            }
            // 2. 导入解析
            List<WorkOrderExcel> dtoList = ExcelUtil.importExcel(file, 1, WorkOrderExcel.class);
            if (dtoList.isEmpty()) {
                return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "没有数据可导入", null, null);
            }
            System.out.println("dtoList:" + dtoList.toString());
            ArrayList<WorkOrder> workOrders = new ArrayList<>();

            for (WorkOrderExcel excel : dtoList) {
                System.out.println("--------execl:" + excel);
                WorkOrder workOrder = WorkOrderExcelConverter.toEntity(excel);
                if (excel.getElevatorName() == null) {
                    ElevatorInfo elevatorInfo = elevatorInfoService.searchElevatorInfo(new SearchDTO().setRescueCode(excel.getRescueCode()));
                    if (elevatorInfo == null)
                        continue;
                    workOrder.setElevatorName(elevatorInfo.getElevatorName());
                    workOrder.setDistrict(elevatorInfo.getDistrict());
                }
                System.out.println("--------workOrder:" + workOrder);
                workOrders.add(workOrder);
                WorkOrder workOrderByOrderNo = workOrderService.getWorkOrderByOrderNo(workOrder.getOrderNo());
                if (workOrderByOrderNo == null) {
                    workOrderService.save(workOrder);
                } else {
                    workOrderService.update(workOrder, new LambdaUpdateWrapper<WorkOrder>().eq(WorkOrder::getOrderNo, workOrder.getOrderNo()));
                }
                //更新工单记录, 0:创建工单，1:派单，2:救援人员响应成功，3:回拨安抚，4救援人员到达现场，5:救援人员救援完成，6:救援回访，7：维修回访，8:维修完成，99:结案
                ArrayList<WorkOrderProgress> list = new ArrayList<>();
                list.add(new WorkOrderProgress().setOrderNo(workOrder.getOrderNo()).setEmployeeId("1090001").setStatus(0).setProgress("创建工单").setResult("成功").setCreateTime(workOrder.getCreateTime())); //创建工单
                if (excel.getDispatchTime() != null) {
                    list.add(new WorkOrderProgress().setOrderNo(workOrder.getOrderNo()).setEmployeeId("1090001").setStatus(1).setProgress("派单").setResult("成功").setCreateTime(excel.getDispatchTime())); //派单
                }
                if (excel.getArrivalTime() != null) {
                    list.add(new WorkOrderProgress().setOrderNo(workOrder.getOrderNo()).setEmployeeId("1090001").setStatus(4).setProgress("到达现场").setResult("成功").setCreateTime(excel.getArrivalTime())); //到达现场时间
                }

                if (workOrder.getOrderType().equals(WorkOrderTypeEnum.FAULT.getCode())) {
                    String result = workOrder.getFailureReason();
                    if (excel.getRescueTime() != null) {
                        list.add(new WorkOrderProgress().setOrderNo(workOrder.getOrderNo()).setEmployeeId("1090001").setProgress("维修完成").setResult("成功").setStatus(8).setCreateTime(excel.getRescueTime())); //救援人员救援完成
                    }
                    if (excel.getFollowUpTime() != null) {
                        list.add(new WorkOrderProgress().setOrderNo(workOrder.getOrderNo()).setEmployeeId("1090001").setProgress("维修回访").setResult("成功").setStatus(7).setCreateTime(excel.getFollowUpTime()).setRemark(result)); //回访时间
                    }
                }
                if (workOrder.getOrderType().equals(WorkOrderTypeEnum.TRAPPED_PEOPLE.getCode())) {
                    String result = "被困人数" + workOrder.getTrappedCount() + "人，" + "受伤人数" + workOrder.getInjuredCount() + "人，" + "疑似死亡人数" + workOrder.getSuspectedDeathCount() + "人";
                    if (excel.getRescueTime() != null) {
                        list.add(new WorkOrderProgress().setOrderNo(workOrder.getOrderNo()).setEmployeeId("1090001").setProgress("救援完成").setResult("成功").setStatus(5).setCreateTime(excel.getRescueTime()).setRemark(result)); //救援人员救援完成
                    }
                    if (excel.getFollowUpTime() != null) {
                        list.add(new WorkOrderProgress().setOrderNo(workOrder.getOrderNo()).setEmployeeId("1090001").setStatus(6).setProgress("救援回访").setResult("成功").setCreateTime(excel.getFollowUpTime())); //回访时间
                    }
                }
                if (excel.getFollowUpTime() != null) {
                    list.add(new WorkOrderProgress().setOrderNo(workOrder.getOrderNo()).setEmployeeId("1090001").setStatus(99).setResult("成功").setProgress("结案").setCreateTime(excel.getCloseTime())); //结案
                }
                workOrderProgressService.saveOrUpdateBatch(list);
            }

//            workOrderService.saveBatch(workOrders);

            return new BaseResponse(HttpStatus.OK.value(), "成功导入 " + dtoList.size() + " 条电梯信息", null, null);
        } catch (Exception e) {
            System.out.println("Excel 导入失败:" + e);
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "导入失败: " + e.getMessage(), null, null);
        }
    }

    @GetMapping("/export/{id}")
    public void exportReport(@PathVariable Long id, HttpServletResponse response) {
        WorkOrder workOrder = workOrderService.getById(id);
        if (workOrder == null) {
            return;
        }

        PropertyInfo propertyInfo = propertyInfoService.getById(workOrder.getUsingUnitId());
        HashMap<Integer, WorkOrderProgress> wpMap = workOrderProgressService.queryMapByOrderNo(workOrder.getOrderNo());

        Map<String, String> replacements = Map.ofEntries(
                new SimpleEntry<>("{{orderName}}", workOrder.getOrderNo()), // todo 工单名称
                new SimpleEntry<>("{{orderNo}}", workOrder.getOrderNo()),
                new SimpleEntry<>("{{createTime}}", DateUtils.format(workOrder.getCreateTime(), DateUtils.DATE_TIME_PATTERN_CHINA)),
                new SimpleEntry<>("{{registerCode}}", workOrder.getRegisterCode()),
                new SimpleEntry<>("{{rescueCode}}", workOrder.getRescueCode()),
                new SimpleEntry<>("{{usingUnit}}", workOrder.getUsingUnit()),
                new SimpleEntry<>("{{usingUnitPhone}}", propertyInfo.getUsingUnitManagerPhone()),
                new SimpleEntry<>("{{maintenanceUnitName}}", workOrder.getMaintenanceUnit()),
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

        for (WorkOrderProgress wp : wpMap.values()) {
            String value = DateUtils.format(wp.getCreateTime(), DateUtils.DATE_TIME_PATTERN_CHINA) + "   " + "工号" + wp.getEmployeeId() + "   " + wp.getResult();
            replacements.put("{{progress" + wp.getStatus() + "}}", wp.getProgress());
        }


        System.out.println("+++++ replacements: " + replacements);
//        String outputPath = "/tmp/report_" + System.currentTimeMillis() + ".docx";
        String outputPath = "/tmp/report_" + System.currentTimeMillis() + ".xlsx";

        try {
//            FileReplace.replaceTextInWordX(replacements, outputPath);
            boolean success = FileReplace.replaceTextInExcel(replacements, outputPath);
            if (!success) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return;
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", "attachment; filename=处置报告.xlsx");
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
//                File tempFile = new File(outputPath);
//                if (tempFile.exists()) {
//                    tempFile.delete();
//                }
            }
        } catch (Exception e) {
            System.out.println("导出文件失败");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            e.printStackTrace();
        }
    }
}