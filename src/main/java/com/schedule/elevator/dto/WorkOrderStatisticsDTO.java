package com.schedule.elevator.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class WorkOrderStatisticsDTO implements Serializable {
    private Long totalEvents;      // 处置事件总数（起）
    private Long trappedEvents;    // 困人（起）
    private Long nonTrappedEvents; // 非困人（起）
    private Long otherEvents;      // 其他（起）
    private Long rescuedPeople;    // 解救被困人数（人）
    private Double avgArrivalTimeForTrapped;   // 困人救援到达现场平均（分钟）
    private Double avgArrivalTimeForNonTrapped; // 非困人救援到达现场平均（分钟）
    private Double avgRescueDuration;          // 实施救援平均用时（分钟）
}
