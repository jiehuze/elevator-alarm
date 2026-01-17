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
    private Integer exportType;    // // 导出类型(1. 月报，2， 半年报，3. 年报，4. 工单导出，5. 电梯信息，6， 维保单位，7， 小区信息)

    @TableField("is_report")
    private Boolean isReport;     // 是否为报告：0-否（普通数据导出），1-是（分析/汇总报告）

    @TableField("file_name")
    private String fileName;      // 文件的名字命名

    @TableField("file_url")
    private String fileUrl;       // 可选：公开访问URL（如OSS/MinIO外链）

    @TableField("status")
    private Integer status;       // 状态：0-排队中，1-处理中，2-成功，3-失败

    @TableField("error_message")
    private String errorMessage;  // 失败时的错误信息

    @TableField("remark")
    private String remark;        // 备注信息（如：仅包含重大事故工单、用于审计等）

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
    private Long fileSizeKb;   // 文件大小（KB）

    @TableField("record_count")
    private Integer recordCount;  // 导出记录数（便于预览）

    // 构造函数
    public ExportTask() {
    }

    public ExportTask(String taskName, Integer exportType, String triggerUserId, String triggerUserName, String remark) {
        this.taskName = taskName;
        this.exportType = exportType;
        this.triggerUserId = triggerUserId;
        this.triggerUserName = triggerUserName;
        this.status = STATUS_QUEUED; // 默认排队中
        this.createdAt = LocalDateTime.now();
        this.remark = remark;
    }

    // 状态常量
    public static final Integer STATUS_QUEUED = 0;    // 排队中
    public static final Integer STATUS_PROCESSING = 1; // 处理中
    public static final Integer STATUS_SUCCESS = 2;    // 成功
    public static final Integer STATUS_FAILED = 3;     // 失败
}
