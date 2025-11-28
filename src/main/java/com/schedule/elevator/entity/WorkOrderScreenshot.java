package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("work_order_screenshot")
public class WorkOrderScreenshot {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;           // 关联工单ID

    private String fileNames;            // 原始文件名
    private String filePath;            // 存储路径（如 /uploads/work_order/xxx.png）
    private String description;         // 图片描述
    private String uploaderName;        // 上传人姓名

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime uploadTime;

    private Integer fileSize;           // 文件大小（字节）
    private String contentType;         // MIME 类型，如 image/jpeg

    @TableLogic
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Integer deleted = 0;        // 逻辑删除：0-正常，1-删除
}