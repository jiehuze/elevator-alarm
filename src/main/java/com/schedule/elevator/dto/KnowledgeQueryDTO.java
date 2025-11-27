package com.schedule.elevator.dto;

import lombok.Data;

@Data
public class KnowledgeQueryDTO {

    private String type;            // 按类型筛选
    private String keyword;         // 标题或内容关键词
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}