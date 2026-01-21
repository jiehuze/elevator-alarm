package com.schedule.elevator.dto;

import lombok.Data;

@Data
public class BrandMarketAnalysisDTO {
    private Long totalBrands;          // 总品牌数
    private Long smallBrandsCount;     // 小品牌数（≤10台）
    private Long totalBrandsAll;       // 统计品牌总数
    private Double smallBrandPercentage; // 小品牌占比
}
