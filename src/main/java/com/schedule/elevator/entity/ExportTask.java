package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 导出任务管理实体
 */
@Data
@Accessors(chain = true)
@TableName(value = "export_task")
public class ExportTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("task_name")
    private String taskName;      // 导出任务名称（如：2025年Q1工单汇总）

    @TableField("export_type")
    private String exportType;    // 导出类型（如：work_order, fault_stat, personnel_list 等）

    @TableField("file_path")
    private String filePath;      // 文件存储路径或URL

    @TableField("file_url")
    private String fileUrl;       // 公开访问URL（如OSS/MinIO外链）

    @TableField("status")
    private Integer status;       // 状态：0-排队中，1-处理中，2-成功，3-失败

    @TableField("error_message")
    private String errorMessage;  // 失败时的错误信息

    @TableField("trigger_user_id")
    private String triggerUserId; // 触发用户ID（工号或系统账号）

    @TableField("trigger_user_name")
    private String triggerUserName; // 触发人姓名

    @TableField("created_at")
    private LocalDateTime createdAt; // 创建时间（即导出请求时间）

    @TableField("started_at")
    private LocalDateTime startedAt; // 实际开始处理时间

    @TableField("completed_at")
    private LocalDateTime completedAt; // 完成时间

    @TableField("file_size_kb")
    private Integer fileSizeKb;   // 文件大小（KB）

    @TableField("record_count")
    private Integer recordCount;  // 导出记录数（便于预览）

    // 构造函数
    public ExportTask() {}

    public ExportTask(String taskName, String exportType, String triggerUserId, String triggerUserName) {
        this.taskName = taskName;
        this.exportType = exportType;
        this.triggerUserId = triggerUserId;
        this.triggerUserName = triggerUserName;
        this.status = 0; // 默认排队中
        this.createdAt = LocalDateTime.now();
    }

    // 状态常量
    public static final Integer STATUS_QUEUED = 0;    // 排队中
    public static final Integer STATUS_PROCESSING = 1; // 处理中
    public static final Integer STATUS_SUCCESS = 2;    // 成功
    public static final Integer STATUS_FAILED = 3;     // 失败
}
