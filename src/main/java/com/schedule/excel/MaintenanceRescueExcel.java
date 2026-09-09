package com.schedule.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(30)
@ContentRowHeight(25)
public class MaintenanceRescueExcel {

    @ExcelProperty("维护单位名称")
    @ColumnWidth(30)
    private String maintenanceUnit;

    @ExcelProperty("救援组")
    @ColumnWidth(30)
    private String teamName;

    @ExcelProperty("救援组组长")
    @ColumnWidth(30)
    private String leaderName;

    @ExcelProperty("组长电话")
    @ColumnWidth(30)
    private String leaderPhone;

    @ExcelProperty("救援组人数")
    @ColumnWidth(30)
    private Long numbers;
}
