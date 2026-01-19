package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.ExportTaskDTO;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.entity.ExportTask;
import com.schedule.elevator.enums.ExportTypeEnum;
import com.schedule.elevator.service.IExportTaskService;
import com.schedule.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/export-task")
@Tag(name = "导出任务管理", description = "导出任务管理相关API")
@RequiredArgsConstructor
public class ExportTaskController {

    @Autowired
    private IExportTaskService exportTaskService;

    /************************************异步操作操作****************************************/
    @PostMapping("/exec-report")
    @Operation(summary = "导出任务")
    public BaseResponse exportTask(@RequestBody SearchDTO task) {
        task.setIsReport(true);
        task.setTimeRange(DateUtils.format(task.getStartTime(), DateUtils.DATE_PATTERN) + "-" + DateUtils.format(task.getEndTime(), DateUtils.DATE_PATTERN));
        task.setQueryConditions(task.toString());
        if (task.getExportType() == 1) {
            exportTaskService.exportMonthlyReportAsync(task);
        }
        return new BaseResponse(HttpStatus.OK.value(), "开始导出，在导出管理中查看", null, null);
    }

    @PostMapping("/exec-workorder-report")
    @Operation(summary = "导出工单报告")
    public BaseResponse exportWorkorderReport(@RequestBody SearchDTO task) {
        exportTaskService.exportWorkOrderReport(task);
        return new BaseResponse(HttpStatus.OK.value(), "开始导出，在导出管理中查看", null, null);
    }

    @PostMapping("/exec-list")
    @Operation(summary = "导出列表")
    public BaseResponse exportInfoList(@RequestBody SearchDTO searchDTO) {
        exportTaskService.exportInfoList(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "开始导出，在导出管理中查看", null, null);
    }

    /************************************基础操作****************************************/
    @PostMapping("/create")
    @Operation(summary = "创建导出任务")
    public BaseResponse createExportTask(@RequestBody ExportTask task) {
        boolean save = exportTaskService.save(task);
        return new BaseResponse(HttpStatus.OK.value(), "创建成功", save, null);
    }

    @PutMapping("/update")
    @Operation(summary = "更新导出任务")
    public BaseResponse updateExportTask(@RequestBody ExportTask task) {
        boolean update = exportTaskService.update(task, new LambdaQueryWrapper<ExportTask>().eq(ExportTask::getId, task.getId()));
        return new BaseResponse(HttpStatus.OK.value(), "更新成功", update, null);
    }

    @GetMapping("/list")
    @Operation(summary = "查询导出任务列表")
    public BaseResponse getExportTasks(
            @ModelAttribute ExportTaskDTO queryDTO,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {

        Page<ExportTask> tasks = exportTaskService.queryExportTasks(queryDTO, current, size);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", tasks, null);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户导出任务列表")
    public BaseResponse getUserExportTasks(@ModelAttribute ExportTaskDTO queryDTO) {

        List<ExportTask> tasks = exportTaskService.getUserExportTasks(queryDTO);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", tasks, null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取导出任务详情")
    public BaseResponse getExportTask(@PathVariable Long id) {
        ExportTask task = exportTaskService.getById(id);
        if (task == null) {
            return new BaseResponse(HttpStatus.NOT_FOUND.value(), "导出任务不存在", null, null);
        }
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", task, null);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除导出任务")
    public BaseResponse deleteExportTask(@PathVariable Long id) {
        try {
            boolean result = exportTaskService.removeById(id);
            return new BaseResponse(HttpStatus.OK.value(), "删除成功", result, null);
        } catch (Exception e) {
            log.error("删除导出任务失败", e);
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "删除失败", null, e.getMessage());
        }
    }

    @PostMapping("/cleanup")
    @Operation(summary = "清理过期导出任务")
    public BaseResponse cleanupExpiredTasks(@RequestParam(defaultValue = "30") int daysAgo) {
        try {
            Long deletedCount = exportTaskService.cleanupExpiredTasks(daysAgo);
            return new BaseResponse(HttpStatus.OK.value(), "清理成功", deletedCount, null);
        } catch (Exception e) {
            log.error("清理过期导出任务失败", e);
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "清理失败", null, e.getMessage());
        }
    }
}
