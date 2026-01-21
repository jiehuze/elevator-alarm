package com.schedule.elevator.dto;

import lombok.Data;

@Data
public class BrandElevatorCountDTO {
    private String brandName;          // 品牌名称
    private Long elevatorCount;        // 电梯数量
}
