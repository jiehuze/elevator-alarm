package com.schedule.elevator.dto;

import lombok.Data;

@Data
public class BrandMarketAnalysisDTO {
    private Long totalBrands;          // 总品牌数
    private Long smallBrandsCount;     // 小品牌数（≤10台的品牌个数）
    private Long totalElevatorCount;   // 所有电梯总台数
    private Double smallBrandPercentage; // 小品牌占比
}
