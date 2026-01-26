package com.schedule.elevator.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderTypeStatisticsDTO {
    private Integer orderType;      // 工单类型
    private Long typeCount;         // 该类型数量
    private Double percentage;      // 占比百分比
}
