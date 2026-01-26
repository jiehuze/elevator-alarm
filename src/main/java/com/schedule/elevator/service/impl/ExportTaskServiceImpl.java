package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.ExportTaskMapper;
import com.schedule.elevator.dto.ExportTaskDTO;
import com.schedule.elevator.dto.ParamDTO;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.*;
import com.schedule.elevator.enums.ExportTypeEnum;
import com.schedule.elevator.enums.WorkOrderStatusEnum;
import com.schedule.elevator.enums.WorkOrderTypeEnum;
import com.schedule.elevator.service.*;
import com.schedule.excel.*;
import com.schedule.utils.DateUtils;
import com.schedule.utils.ExcelUtil;
import com.schedule.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportTaskServiceImpl extends ServiceImpl<ExportTaskMapper, ExportTask> implements IExportTaskService {

    private final ExportTaskMapper exportTaskMapper;

    @Autowired
    private IWorkOrderService workOrderService;

    @Autowired
    private IWordExportService wordExportService;

    @Autowired
    private IPropertyInfoService propertyInfoService;

    @Autowired
    private IWorkOrderProgressService workOrderProgressService;

    @Autowired
    private IElevatorInfoService elevatorInfoService;

    @Autowired
    private ICommunityService communityService;

    @Autowired
    private IMaintenanceUnitService maintenanceUnitService;

    @Autowired
    private IMaintenanceTeamService maintenanceTeamService;

    @Autowired
    private IMaintenancePersonnelService maintenancePersonnelService;

    @Autowired
    private ParamDTO paramDTO;

    @Override
    public ExportTask createExportTask(ExportTask task) {
        save(task);
        log.info("创建导出任务: id={}, type={}, user={}", task.getId(), task.getExportType(), task.getTriggerUserId());
        return task;
    }

    @Override
    public Page<ExportTask> queryExportTasks(ExportTaskDTO queryDTO, int current, int size) {
        Page<ExportTask> page = new Page<>(current, size);

        LambdaQueryWrapper<ExportTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(queryDTO.getTaskName()), ExportTask::getTaskName, queryDTO.getTaskName());
        queryWrapper.eq(queryDTO.getExportType() != null, ExportTask::getExportType, queryDTO.getExportType());
        queryWrapper.eq(queryDTO.getStatus() != null, ExportTask::getStatus, queryDTO.getStatus());
        queryWrapper.eq(StringUtils.isNotBlank(queryDTO.getTriggerUserId()), ExportTask::getTriggerUserId, queryDTO.getTriggerUserId());
        queryWrapper.eq(queryDTO.getIsReport() != null, ExportTask::getIsReport, queryDTO.getIsReport());
        if (queryDTO.getStartTime() != null && queryDTO.getEndTime() != null) {
            queryWrapper.between(ExportTask::getCreatedAt, queryDTO.getStartTime(), queryDTO.getEndTime());
        }

        queryWrapper.orderByDesc(ExportTask::getCreatedAt);

        return this.page(page, queryWrapper);
    }

    @Override
    public boolean updateToProcessing(Long taskId) {
        ExportTask task = new ExportTask();
        task.setId(taskId);
        task.setStatus(ExportTask.STATUS_PROCESSING);
        task.setStartedAt(LocalDateTime.now());
        boolean result = updateById(task);
        log.info("更新任务为处理中: taskId={}, success={}", taskId, result);
        return result;
    }

    @Override
    public boolean updateToSuccess(Long taskId, String fileName, String fileUrl, Long fileSizeKb, Integer recordCount) {
        ExportTask task = new ExportTask();
        task.setId(taskId);
        task.setStatus(ExportTask.STATUS_SUCCESS);
        task.setFileName(fileName);
        task.setFileUrl(fileUrl);
        task.setFileSizeKb(fileSizeKb);
        task.setRecordCount(recordCount);
        task.setCompletedAt(LocalDateTime.now());
        boolean result = updateById(task);
        log.info("更新任务为成功: taskId={}, success={}", taskId, result);
        return result;
    }

    @Override
    public boolean updateToFailed(Long taskId, String errorMessage) {
        ExportTask task = new ExportTask();
        task.setId(taskId);
        task.setStatus(ExportTask.STATUS_FAILED);
        task.setErrorMessage(errorMessage);
        task.setCompletedAt(LocalDateTime.now());
        boolean result = updateById(task);
        log.error("更新任务为失败: taskId={}, error={}", taskId, errorMessage);
        return result;
    }

    @Override
    public List<ExportTask> getUserExportTasks(ExportTaskDTO queryDTO) {
        LambdaQueryWrapper<ExportTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(queryDTO.getTaskName()), ExportTask::getTaskName, queryDTO.getTaskName());
        queryWrapper.eq(queryDTO.getExportType() != null, ExportTask::getExportType, queryDTO.getExportType());
        queryWrapper.eq(queryDTO.getStatus() != null, ExportTask::getStatus, queryDTO.getStatus());
        queryWrapper.eq(StringUtils.isNotBlank(queryDTO.getTriggerUserId()), ExportTask::getTriggerUserId, queryDTO.getTriggerUserId());
        queryWrapper.eq(queryDTO.getIsReport() != null, ExportTask::getIsReport, queryDTO.getIsReport());
        if (queryDTO.getStartTime() != null && queryDTO.getEndTime() != null) {
            queryWrapper.between(ExportTask::getCreatedAt, queryDTO.getStartTime(), queryDTO.getEndTime());
        }

        queryWrapper.orderByDesc(ExportTask::getCreatedAt);
        return list(queryWrapper);
    }

    @Override
    public Long cleanupExpiredTasks(int daysAgo) {
        LocalDateTime expiredTime = LocalDateTime.now().minusDays(daysAgo);
        QueryWrapper<ExportTask> wrapper = new QueryWrapper<>();
        wrapper.le("created_at", expiredTime);
        long deletedCount = this.count(wrapper);
        boolean remove = remove(wrapper);

        log.info("清理过期导出任务: {}天前的记录，删除", daysAgo, deletedCount);
        return deletedCount;
    }

    @Override
//    @Async
    public void exportMonthlyReportAsync(SearchDTO task) {
        System.out.println("开始导出数据");
        ExportTask exportTask = createExportTask(task);
        try {
            updateToProcessing(exportTask.getId());
            SearchDTO searchDTO = new SearchDTO().setCreateTimeStart(task.getStartTime()).setCreateTimeEnd(task.getEndTime()).setDistrict(task.getDistrict());
            String fileName = "month-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + ".docx";
            String urlPath = paramDTO.getReportPath() + fileName;
            String filePath = paramDTO.getRootPath() + urlPath;
            FileUtil.ensureDirectoryExists(filePath);
            wordExportService.generateMonthlyReport(searchDTO, filePath);

            updateToSuccess(exportTask.getId(), fileName, urlPath, FileUtil.getFileSizeInKB(filePath), 0);
            System.out.println("数据导出完成！");
        } catch (Exception e) {
            e.printStackTrace();
            updateToFailed(exportTask.getId(), e.getMessage());
            System.out.println("数据导出失败");
        }
    }

    @Override
//    @Async
    public void exportYearReportAsync(SearchDTO task) {
        System.out.println("开始导出数据");
        ExportTask exportTask = createExportTask(task);
        try {
            updateToProcessing(exportTask.getId());
            SearchDTO searchDTO = new SearchDTO().setCreateTimeStart(task.getStartTime()).setCreateTimeEnd(task.getEndTime()).setDistrict(task.getDistrict());
            String fileName = "year-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + ".docx";
            String urlPath = paramDTO.getReportPath() + fileName;
            String filePath = paramDTO.getRootPath() + urlPath;
            FileUtil.ensureDirectoryExists(filePath);
            wordExportService.generateYearlyReport(searchDTO, filePath);

            updateToSuccess(exportTask.getId(), fileName, urlPath, FileUtil.getFileSizeInKB(filePath), 0);
            System.out.println("数据导出完成！");
        } catch (Exception e) {
            e.printStackTrace();
            updateToFailed(exportTask.getId(), e.getMessage());
            System.out.println("数据导出失败");
        }
    }

    @Override
    @Async
    public void exportWorkOrderReport(SearchDTO task) {
        ExportTask exportTask = createExportTask(task);
        updateToProcessing(exportTask.getId());

        WorkOrder workOrder = workOrderService.getWorkOrderByOrderNo(task.getOrderNo());

        PropertyInfo propertyInfo = propertyInfoService.getById(workOrder.getUsingUnitId());
        List<WorkOrderProgress> workOrderProgresses = workOrderProgressService.queryByOrderNo(workOrder.getOrderNo());

        String description = WorkOrderTypeEnum.getByCode(workOrder.getOrderType()).getDescription();
        String format = DateUtils.format(workOrder.getCreateTime(), DateUtils.DATE_PATTERN);

        Map<String, String> replaceMap = new HashMap<>(); // 创建可变HashMap
        replaceMap.put("orderName", format + workOrder.getProjectName() + description); // 工单名称
        replaceMap.put("orderNo", workOrder.getOrderNo());
        replaceMap.put("createTime", DateUtils.format(workOrder.getCreateTime(), DateUtils.DATE_TIME_PATTERN_CHINA));
        replaceMap.put("registerCode", workOrder.getRegisterCode());
        replaceMap.put("rescueCode", workOrder.getRescueCode());
        replaceMap.put("usingUnit", workOrder.getUsingUnit());
        replaceMap.put("usingUnitPhone", propertyInfo.getUsingUnitManagerPhone());
        replaceMap.put("maintenanceUnitName", workOrder.getMaintenanceUnit());
        replaceMap.put("maintenanceUnitPhone", workOrder.getMaintenancePersonnelPhone());
        replaceMap.put("elevatorAddress", workOrder.getElevatorAddress());
        replaceMap.put("alarmPersonName", workOrder.getAlarmPersonName());
        replaceMap.put("alarmPersonPhone", workOrder.getAlarmPersonPhone());
        replaceMap.put("incidentDescription", workOrder.getIncidentDescription());
        replaceMap.put("trappedCount", workOrder.getTrappedCount().toString());
        replaceMap.put("injuredCount", workOrder.getInjuredCount().toString());
        replaceMap.put("suspectedDeathCount", workOrder.getSuspectedDeathCount().toString());
        replaceMap.put("alarmTime", DateUtils.format(workOrder.getAlarmTime(), DateUtils.DATE_TIME_PATTERN_CHINA));
        replaceMap.put("major", workOrder.getMajorIncident() ? "是" : "否");
        replaceMap.put("rescue", workOrder.getMedicalRescueStarted() ? "是" : "否");

        int index = 0;
        for (WorkOrderProgress wp : workOrderProgresses) {
            if (wp.getStatus() == WorkOrderStatusEnum.RESCUE_ARRIVED.getCode()) {
                replaceMap.put("arrivalTime", DateUtils.format(wp.getUpdateTime(), DateUtils.DATE_TIME_PATTERN_CHINA)); //到达现场时间
            }
            if (wp.getStatus() == WorkOrderStatusEnum.RESCUE_COMPLETED.getCode()) {
                replaceMap.put("completeTime", DateUtils.format(wp.getUpdateTime(), DateUtils.DATE_TIME_PATTERN_CHINA)); //救援完成时间
            }
            String value = DateUtils.format(wp.getCreateTime(), DateUtils.DATE_TIME_PATTERN_CHINA) + "   " + "工号" + wp.getEmployeeId() + wp.getProgress() + "   " + wp.getResult();
            replaceMap.put("progress" + index, value);
            index++;
        }

        String arrivalTime = replaceMap.get("arrivalTime");
        if (arrivalTime == null) {
            replaceMap.put("arrivalTime", "无");
        }
        String completeTime = replaceMap.get("completeTime");
        if (completeTime == null) {
            replaceMap.put("completeTime", "无");
        }

        for (int i = index; i < WorkOrderStatusEnum.values().length; i++) {
            replaceMap.put("progress" + i, "");
        }

        try {
            String fileName = "workorder-" + task.getOrderNo() + ".docx";
            String urlPath = paramDTO.getReportPath() + fileName;
            String filePath = paramDTO.getRootPath() + urlPath;
            FileUtil.ensureDirectoryExists(filePath);

            InputStream inputStream = new ClassPathResource("doc/workorder.docx").getInputStream();
            DocxPlaceholderReplaceUtil.replacePlaceholder(inputStream, replaceMap, filePath);
            updateToSuccess(exportTask.getId(), fileName, urlPath, FileUtil.getFileSizeInKB(filePath), 0);
            System.out.println("数据导出完成！");
        } catch (Exception e) {
            updateToFailed(exportTask.getId(), e.getMessage());
            System.out.println("数据导出失败");
            throw new RuntimeException(e);
        }
    }

    @Override
    @Async
    public void exportInfoList(SearchDTO searchDTO) {
        ExportTask exportTask = createExportTask(searchDTO);
        updateToProcessing(exportTask.getId());

        if (searchDTO.getExportType() == ExportTypeEnum.WORK_ORDER_LIST.getCode()) { //工单列表
            try {
                String fileName = "workorder-list-" + DateUtils.format(LocalDateTime.now(), "yyMMddHHmmss") + ".xlsx";
                String urlPath = paramDTO.getExportPath() + fileName;
                String filePath = paramDTO.getRootPath() + urlPath;
                FileUtil.ensureDirectoryExists(filePath);

                LocalDateTime dispatchTime = null, arriveTime = null, rescueTime = null, followUpTime = null, closeTime = null;
                ArrayList<WorkOrderExcel> dtoList = new ArrayList<>();

                List<WorkOrder> workOrders = workOrderService.queryByConditions(searchDTO);
                for (WorkOrder workOrder : workOrders) {
                    HashMap<Integer, WorkOrderProgress> progressHashMap = workOrderProgressService.queryMapByOrderNo(workOrder.getOrderNo());
                    WorkOrderProgress dispatch = progressHashMap.get(WorkOrderStatusEnum.DISPATCHED.getCode());
                    if (dispatch != null) {
                        dispatchTime = dispatch.getCreateTime();
                    }
                    WorkOrderProgress arrive = progressHashMap.get(WorkOrderStatusEnum.RESCUE_ARRIVED.getCode());
                    if (arrive != null) {
                        arriveTime = arrive.getCreateTime();
                    }
                    WorkOrderProgress rescue = progressHashMap.get(WorkOrderStatusEnum.RESCUE_COMPLETED.getCode());
                    if (rescue != null) {
                        rescueTime = rescue.getCreateTime();
                    }
                    WorkOrderProgress followUp = progressHashMap.get(WorkOrderStatusEnum.RESCUE_FOLLOW_UP.getCode());
                    if (followUp != null) {
                        followUpTime = followUp.getCreateTime();
                    }
                    WorkOrderProgress close = progressHashMap.get(WorkOrderStatusEnum.CLOSED.getCode());
                    if (close != null) {
                        closeTime = close.getCreateTime();
                    }

                    dtoList.add(WorkOrderExcelConverter.toDto(workOrder, dispatchTime, arriveTime, rescueTime, followUpTime, closeTime));
                }

                System.out.println("list size:" + dtoList.toString());

                // 写入 Excel
                ExcelUtil.exportExcelToTargetWithTemplate(filePath, fileName, "历史工单", dtoList, WorkOrderExcel.class, "doc/workorder.xlsx");
                updateToSuccess(exportTask.getId(), fileName, urlPath, FileUtil.getFileSizeInKB(filePath), 0);
            } catch (Exception e) {
                updateToFailed(exportTask.getId(), e.getMessage());
                throw new RuntimeException(e);
            }

        } else if (searchDTO.getExportType() == ExportTypeEnum.ELEVATOR_INFO_LIST.getCode()) { //电梯信息列表
            try {

//                String fileName = URLEncoder.encode("电梯信息列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");

                List<ElevatorInfo> list = elevatorInfoService.listElevators(searchDTO); // 从数据库查所有
                List<ElevatorImportTemplateExcel> dtoList = new ArrayList<>();
                for (ElevatorInfo info : list) {
                    Community community = communityService.getById(info.getCommunityId());
                    PropertyInfo propertyInfo = propertyInfoService.getById(info.getUsingUnitId());
                    MaintenanceUnit maintenanceUnit = maintenanceUnitService.getById(info.getMaintenanceUnitId());
                    MaintenanceTeam maintenanceTeam = maintenanceTeamService.getById(info.getMaintenanceTeamId());
                    MaintenancePersonnel maintenancePersonnel = maintenancePersonnelService.getById(info.getMaintenancePersonnelId());

                    ElevatorImportTemplateExcel dto = ElevatorImportExcelConverter.toDTO(info, community, propertyInfo, maintenanceUnit, maintenanceTeam, maintenancePersonnel);

                    dtoList.add(dto);
                }

                System.out.println("list size:" + dtoList.toString());

                String fileName = "workorder-list-" + DateUtils.format(LocalDateTime.now(), "yyMMddHHmmss") + ".xlsx";
                String urlPath = paramDTO.getExportPath() + fileName;
                String filePath = paramDTO.getRootPath() + urlPath;
                FileUtil.ensureDirectoryExists(filePath);
                // 写入 Excel
                ExcelUtil.exportExcelToTargetWithTemplate(filePath, fileName, "电梯信息", dtoList, ElevatorImportTemplateExcel.class, "doc/elevator.xlsx");
                updateToSuccess(exportTask.getId(), fileName, urlPath, FileUtil.getFileSizeInKB(filePath), 0);
            } catch (Exception e) {
                updateToFailed(exportTask.getId(), e.getMessage());
                throw new RuntimeException(e);
            }
        } else if (searchDTO.getExportType() == ExportTypeEnum.MAINTENANCE_UNIT_LIST.getCode()) { //维保单位列表
            try {
                // 设置响应头
//                String fileName = URLEncoder.encode("维保单位信息", StandardCharsets.UTF_8).replaceAll("\\+", "%20");

                List<MaintenanceUnit> dtoList = maintenanceUnitService.listByQuery(searchDTO);
                System.out.println("list size:" + dtoList.toString());

                String fileName = "workorder-list-" + DateUtils.format(LocalDateTime.now(), "yyMMddHHmmss") + ".xlsx";
                String urlPath = paramDTO.getExportPath() + fileName;
                String filePath = paramDTO.getRootPath() + urlPath;
                FileUtil.ensureDirectoryExists(filePath);
                // 写入 Excel
                ExcelUtil.exportExcelToTargetWithTemplate(filePath, fileName, "维保信息", dtoList, MaintenanceUnitExcel.class, "doc/maintenance_unit.xlsx");
                updateToSuccess(exportTask.getId(), fileName, urlPath, FileUtil.getFileSizeInKB(filePath), 0);
            } catch (Exception e) {
                updateToFailed(exportTask.getId(), e.getMessage());
                throw new RuntimeException(e);
            }
        } else if (searchDTO.getExportType() == ExportTypeEnum.MAINTENANCE_PERSONNEL_LIST.getCode()) { //维保人员列表
            try {
//            String fileName = URLEncoder.encode("维修人员信息", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
                List<MaintenancePersonnel> list = maintenancePersonnelService.listBySearchDTO(searchDTO);
                List<MaintenancePersonnelExcel> dtoList = new ArrayList<>();
                for (MaintenancePersonnel personnel : list) {
                    MaintenancePersonnelExcel dto = MaintenanceExcelConverter.toPersonDto(personnel);
                    dtoList.add(dto);
                }

                System.out.println("list size:" + dtoList.toString());
                String fileName = "workorder-person-list-" + DateUtils.format(LocalDateTime.now(), "yyMMddHHmmss") + ".xlsx";
                String urlPath = paramDTO.getExportPath() + fileName;
                String filePath = paramDTO.getRootPath() + urlPath;
                FileUtil.ensureDirectoryExists(filePath);
                // 写入 Excel
                ExcelUtil.exportExcelToTargetWithTemplate(filePath, fileName, "维保人信息", dtoList, MaintenancePersonnelExcel.class, "doc/maintenance_person.xlsx");
                updateToSuccess(exportTask.getId(), fileName, urlPath, FileUtil.getFileSizeInKB(filePath), 0);
            } catch (Exception e) {
                updateToFailed(exportTask.getId(), e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
