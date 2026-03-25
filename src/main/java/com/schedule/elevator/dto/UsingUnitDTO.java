package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UsingUnitDTO {

    @ExcelProperty("单位名称")
    private String usingUnitName;

    @ExcelProperty("负责人")
    private String managerName;

    @ExcelProperty("负责人电话")
    private String managerPhone;

    @ExcelProperty("电梯数")
    private Long elevatorCount;

    @ExcelProperty("安全员数量")
    private Long safetyOfficerCount;
}
