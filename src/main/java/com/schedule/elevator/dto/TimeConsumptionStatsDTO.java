package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TimeConsumptionStatsDTO {
    @ExcelProperty("时间范围")
    private String timeRange;  // 时间段如 "0-5分钟"

    @ExcelProperty("困人事件到场时间（起）")
    private Integer trappedArrivalCount;  // 困人事件到场次数

    @ExcelProperty("非困人事件到场时间（起）")
    private Integer nonTrappedArrivalCount;  // 非困人事件到场次数

    @ExcelProperty("困人实施救援（起）")
    private Integer trappedRescueCount;  // 困人实施救援次数
}
