package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@HeadRowHeight(25)
@ContentRowHeight(20)
public class WorkOrderStatisticsDTO implements Serializable {

    @ExcelProperty("月份")
    @ColumnWidth(25)
    private String month;

    @ExcelProperty("处置事件总数（起）")
    @ColumnWidth(25)
    private Long totalEvents = 0L;      // 处置事件总数（起）

    @ExcelProperty("困人（起）")
    @ColumnWidth(25)
    private Long trappedEvents = 0L;    // 困人（起）

    @ExcelProperty("非困人（起）")
    @ColumnWidth(25)
    private Long nonTrappedEvents = 0L; // 非困人（起）

    @ExcelProperty("其他（起）")
    @ColumnWidth(25)
    private Long otherEvents = 0L;      // 其他（起）

    @ExcelProperty("解救被困人数（人）")
    @ColumnWidth(25)
    private Long rescuedPeople = 0L;    // 解救被困人数（人）

    @ExcelProperty("困人救援到达现场平均（分钟）")
    @ColumnWidth(25)
    private Double avgArrivalTimeForTrapped = 0.0;   // 困人救援到达现场平均（分钟）

    @ExcelProperty("非困人救援到达现场平均（分钟）")
    @ColumnWidth(25)
    private Double avgArrivalTimeForNonTrapped = 0.0; // 非困人救援到达现场平均（分钟）

    @ExcelProperty("实施救援平均用时（分钟）")
    @ColumnWidth(25)
    private Double avgRescueDuration = 0.0;          // 实施救援平均用时（分钟）

    @ExcelProperty("故障维修平均用时（分钟）")
    @ColumnWidth(25)
    private Double avgRepairDuration = 0.0;          // 故障维修平均用时（分钟）

}
