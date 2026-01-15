package com.schedule.elevator.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class SysDistrictDTO implements Serializable {
    
    private Long id;
    
    @ExcelProperty("区域编码")
    private String districtCode;
    
    @ExcelProperty("区域名称")
    private String districtName;
    
    @ExcelProperty("上级区域编码")
    private String parentCode;
    
    @ExcelProperty("区域级别")
    private Integer districtLevel;
    
    @ExcelProperty("排序")
    private Integer sort;
    
    @ExcelProperty("是否启用")
    private Boolean enabled;
    
    @ExcelProperty("备注")
    private String remark;
}
