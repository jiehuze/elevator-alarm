package com.schedule.elevator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.elevator.dao.mapper.ExportTaskMapper;
import com.schedule.elevator.dto.ExportTaskQueryDTO;
import com.schedule.elevator.entity.ExportTask;
import com.schedule.elevator.service.IExportTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.analysis.function.Exp;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportTaskServiceImpl extends ServiceImpl<ExportTaskMapper, ExportTask> implements IExportTaskService {

    private final ExportTaskMapper exportTaskMapper;

    @Override
    public ExportTask createExportTask(String taskName, String exportType, String triggerUserId, String triggerUserName) {
        ExportTask task = new ExportTask(taskName, exportType, triggerUserId, triggerUserName);
        save(task);
        log.info("创建导出任务: id={}, type={}, user={}", task.getId(), exportType, triggerUserId);
        return task;
    }

    @Override
    public Page<ExportTask> queryExportTasks(ExportTaskQueryDTO queryDTO, int current, int size) {
        Page<ExportTask> page = new Page<>(current, size);

        LambdaQueryWrapper<ExportTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(queryDTO.getTaskName()), ExportTask::getTaskName, queryDTO.getTaskName());
        queryWrapper.eq(StringUtils.isNotBlank(queryDTO.getExportType()), ExportTask::getExportType, queryDTO.getExportType());
        queryWrapper.eq(queryDTO.getStatus() != null, ExportTask::getStatus, queryDTO.getStatus());
        queryWrapper.eq(StringUtils.isNotBlank(queryDTO.getTriggerUserId()), ExportTask::getTriggerUserId, queryDTO.getTriggerUserId());
        if (queryDTO.getStartDate() != null && queryDTO.getEndDate() != null) {
            queryWrapper.between(ExportTask::getCreatedAt, queryDTO.getStartDate(), queryDTO.getEndDate());
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
    public boolean updateToSuccess(Long taskId, String filePath, String fileUrl, Integer fileSizeKb, Integer recordCount) {
        ExportTask task = new ExportTask();
        task.setId(taskId);
        task.setStatus(ExportTask.STATUS_SUCCESS);
        task.setFilePath(filePath);
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
    public List<ExportTask> getUserExportTasks(ExportTaskQueryDTO queryDTO) {
        LambdaQueryWrapper<ExportTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(queryDTO.getTaskName()), ExportTask::getTaskName, queryDTO.getTaskName());
        queryWrapper.eq(StringUtils.isNotBlank(queryDTO.getExportType()), ExportTask::getExportType, queryDTO.getExportType());
        queryWrapper.eq(queryDTO.getStatus() != null, ExportTask::getStatus, queryDTO.getStatus());
        queryWrapper.eq(StringUtils.isNotBlank(queryDTO.getTriggerUserId()), ExportTask::getTriggerUserId, queryDTO.getTriggerUserId());
        if (queryDTO.getStartDate() != null && queryDTO.getEndDate() != null) {
            queryWrapper.between(ExportTask::getCreatedAt, queryDTO.getStartDate(), queryDTO.getEndDate());
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

    private QueryWrapper<ExportTask> buildQueryWrapper(ExportTaskQueryDTO queryDTO) {
        QueryWrapper<ExportTask> wrapper = new QueryWrapper<>();

        if (queryDTO.getExportType() != null && !queryDTO.getExportType().isEmpty()) {
            wrapper.eq("export_type", queryDTO.getExportType());
        }

        if (queryDTO.getStatus() != null) {
            wrapper.eq("status", queryDTO.getStatus());
        }

        if (queryDTO.getTriggerUserId() != null && !queryDTO.getTriggerUserId().isEmpty()) {
            wrapper.eq("trigger_user_id", queryDTO.getTriggerUserId());
        }

        if (queryDTO.getStartDate() != null) {
            wrapper.ge("created_at", queryDTO.getStartDate());
        }

        if (queryDTO.getEndDate() != null) {
            wrapper.le("created_at", queryDTO.getEndDate());
        }

        wrapper.orderByDesc("created_at");
        return wrapper;
    }
}
