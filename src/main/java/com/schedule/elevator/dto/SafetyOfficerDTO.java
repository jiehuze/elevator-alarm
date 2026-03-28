package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors
public class SafetyOfficerDTO {
    @ExcelProperty("安全员姓名")
    private String safetyOfficerName;

    @ExcelProperty("安全员手机")
    private String safetyOfficerPhone;

    @ExcelProperty("使用单位")
    private String usingUnit;

    @ExcelProperty("在职状态")
    private String status;

    @ExcelProperty("项目名称")
    private String projectName;

    @ExcelProperty("电梯数")
    private Long elevatorNum;

}
