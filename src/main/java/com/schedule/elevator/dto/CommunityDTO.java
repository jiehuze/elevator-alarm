package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommunityDTO {

    @ExcelProperty("项目名称")
    private String projectName;

    @ExcelProperty("区县")
    private String district;

    @ExcelProperty("项目类型")
    private String projectType;

    @ExcelProperty("电梯数")
    private Long elevatorCount;
}
