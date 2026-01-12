package com.schedule.elevator.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProjectTypeStatItemDTO {
    //    private String typeCode;      // 存储 code，如 "RESIDENTIAL"
//    private String typeName;      // 显示用，如 "住宅小区"
//    private Integer count;
//    private BigDecimal percentage; // 0.00 ~ 100.00
    private Long total;
    private List<ProjectTypeCountDTO> projectTypeCounts;
}
