package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BrandFaultStatsDTO {
    @ExcelProperty("序号")
    private Integer idx;

    @ExcelProperty("电梯品牌")
    private String brand;

    @ExcelProperty("故障数")
    private Integer faultCount;
}
