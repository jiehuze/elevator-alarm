package com.schedule.elevator.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BrandElevatorStatisticsDTO extends BrandMarketAnalysisDTO {
    private String top5Brands; // top5品牌
    private BigDecimal top5Percentage; // top5品牌占比
}
