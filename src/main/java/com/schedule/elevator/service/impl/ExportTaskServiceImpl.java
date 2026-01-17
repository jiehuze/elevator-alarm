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
import com.schedule.elevator.entity.ExportTask;
import com.schedule.elevator.service.IExportTaskService;
import com.schedule.elevator.service.IWordExportService;
import com.schedule.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportTaskServiceImpl extends ServiceImpl<ExportTaskMapper, ExportTask> implements IExportTaskService {

    private final ExportTaskMapper exportTaskMapper;
    @Autowired
    private IWordExportService wordExportService;
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
        if (queryDTO.getStartTime() != null && queryDTO.getEndTime() != null) {
            queryWrapper.between(ExportTask::getCreatedAt, queryDTO.getStartTime(), queryDTO.getEndTime());
        }

        queryWrapper.orderByAsc(ExportTask::getCreatedAt);

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
    @Async
    public void exportMonthlyReportAsync(ExportTaskDTO task) {
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
            System.out.println("数据导出被中断");
        }
    }

    @Override
    public void exportYearReportAsync(ExportTaskDTO task) {
        return;
    }
}
