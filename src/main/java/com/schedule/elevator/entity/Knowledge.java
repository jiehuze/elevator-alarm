package com.schedule.elevator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("knowledge")
public class Knowledge implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;           // 文章标题
    private String type;            // 类型：困人话术 / 故障话术 / 救援常识
    private String content;         // 正文内容
    private String script;          // 标准话术（关键字段）
    private String fileUrl;         // 文件地址（如 PDF、音频等）
    private String creatorName;     // 创建人姓名

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}