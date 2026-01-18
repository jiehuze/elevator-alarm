package com.schedule.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(30)
@ContentRowHeight(25)
public class MaintenancePersonnelExcel {

    @ExcelProperty("姓名")
    @ColumnWidth(30)
    private String name;

    @ExcelProperty("手机号")
    @ColumnWidth(30)
    private String phone;

    @ExcelProperty("维保单位名称")
    @ColumnWidth(50)
    private String company;

//    @ExcelProperty("维保组")
//    @ColumnWidth(15)
//    private Long maintenanceTeam;
//
//    @ExcelProperty("二级维保组")
//    @ColumnWidth(15)
//    private Long subMaintenanceTeamId;

    @ExcelProperty(value = "在岗状态") // 需要自定义转换器
    @ColumnWidth(30)
    private String status; // 1=在岗，0=离岗

    @ExcelProperty("创建时间")
    @ColumnWidth(30)
    private String createdAt;

    @ExcelProperty("更新时间")
    @ColumnWidth(30)
    private String updatedAt;
}
