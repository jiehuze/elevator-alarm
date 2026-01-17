package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.entity.ExportTask;
import com.schedule.elevator.dto.ExportTaskDTO;

import java.util.List;

public interface IExportTaskService extends IService<ExportTask> {

    /**
     * 创建导出任务
     */
    ExportTask createExportTask(ExportTask exportTask);

    /**
     * 分页查询导出任务
     */
    Page<ExportTask> queryExportTasks(ExportTaskDTO queryDTO, int current, int size);

    /**
     * 更新任务为处理中状态
     */
    boolean updateToProcessing(Long taskId);

    /**
     * 更新任务为成功状态
     */
    boolean updateToSuccess(Long taskId, String filePath, String fileUrl, Long fileSizeKb, Integer recordCount);

    /**
     * 更新任务为失败状态
     */
    boolean updateToFailed(Long taskId, String errorMessage);

    /**
     * 获取用户的导出任务列表
     */
    List<ExportTask> getUserExportTasks(ExportTaskDTO queryDTO);

    /**
     * 清理过期的导出任务记录
     */
    Long cleanupExpiredTasks(int daysAgo);

    /**
     * 导出数据
     */
    void exportMonthlyReportAsync(ExportTaskDTO task);

    /**
     * 导出数据
     */
    void exportYearReportAsync(ExportTaskDTO task);

    /**
     * 导出数据
     */
    void exportWorkOrderReport(ExportTaskDTO task);

}
