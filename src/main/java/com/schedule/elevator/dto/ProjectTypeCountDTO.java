package com.schedule.elevator.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ProjectTypeCountDTO {
    private String projectCode;   // 对应 project_type 字段值
    private String projectName;      // 显示用，如 "住宅小区"
    private Long count;
    private BigDecimal percentage; // 0.00 ~ 100.00
}
