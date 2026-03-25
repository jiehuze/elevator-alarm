package com.schedule.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@HeadRowHeight(30)
@ContentRowHeight(25)
public class MaintenanceUnitExcel {

    @ExcelProperty("维护单位名称")
    @ColumnWidth(25)
    private String maintenanceUnit;

    @ExcelProperty("维保单位电话")
    @ColumnWidth(12)
    private String maintenanceUnitPhone;

    @ExcelProperty("维保单位负责人")
    @ColumnWidth(25)
    private String maintenanceUnitManager;

    @ExcelProperty("维保单位负责人电话")
    @ColumnWidth(25)
    private String maintenanceUnitManagerPhone;

    @ExcelProperty("维护公司地址")
    @ColumnWidth(30)
    private String companyAddress;

    @ExcelProperty("维保单位类型")
    @ColumnWidth(25)
    private String maintenanceUnitType;

    @ExcelProperty("维保单位状态")
    @ColumnWidth(25)
    private String maintenanceUnitStatus;

    @ExcelProperty("维保单位编码")
    @ColumnWidth(30)
    private String maintenanceUnitCode;

    @ExcelProperty("电梯数")
    @ColumnWidth(15)
    private Integer count;

    @ExcelProperty("维保人数")
    @ColumnWidth(15)
    private Integer personCount;
}
