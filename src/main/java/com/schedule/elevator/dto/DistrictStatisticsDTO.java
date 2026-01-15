package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class DistrictStatisticsDTO implements Serializable {
    
    @ExcelProperty("区域名称")
    private String district;
    
    @ExcelProperty("电梯数")
    private Integer elevatorCount;
    
    @ExcelProperty("故障总数")
    private Integer totalFaults;
    
    @ExcelProperty("困人故障（机械）")
    private Integer trappedMechanicalFaults;
    
    @ExcelProperty("困人故障（非机械）")
    private Integer trappedNonMechanicalFaults;
    
    @ExcelProperty("非困故障（机械）")
    private Integer nonTrappedMechanicalFaults;
    
    @ExcelProperty("非困故障（非机械）")
    private Integer nonTrappedNonMechanicalFaults;
    
    @ExcelProperty("其他")
    private Integer otherFaults;
    
    @ExcelProperty("人员伤亡数（受伤和死亡人数的和）")
    private Integer casualtyCount;
}
