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
public class TimeSlotStatsDTO implements Serializable {
    @ExcelProperty("时段")
    @ColumnWidth(25)
    private String timeSlot;   // 如 "0-2"

    @ExcelProperty("故障数量")
    @ColumnWidth(25)
    private Integer count;     // 故障次数

    @ExcelProperty("困人故障")
    @ColumnWidth(25)
    private Integer trappedCount;    // 困人故障数量

    @ExcelProperty("非困人故障")
    private Integer nonTrappedCount; // 非困人故障数量

    @ExcelProperty("其他")
    private Integer otherCount;      // 其他故障数量

    @ExcelProperty("故障率")
    private Double failureRate;      // 故障率
}
