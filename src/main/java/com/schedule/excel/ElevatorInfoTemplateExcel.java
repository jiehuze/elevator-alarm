package com.schedule.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import com.schedule.utils.BigDecimalStringConverter;
import lombok.Data;

import java.math.BigDecimal;

@Data
@HeadRowHeight(25)
@ContentRowHeight(25)
@ContentStyle(wrapped = BooleanEnum.TRUE,
        horizontalAlignment = HorizontalAlignmentEnum.CENTER,  // 水平居中
        verticalAlignment = VerticalAlignmentEnum.CENTER       // 垂直居中
)
public class ElevatorInfoTemplateExcel {

    @ExcelProperty("电梯编号")
    @ColumnWidth(20)
    private String elevatorNo;

    @ExcelProperty("省份")
    @ColumnWidth(15)
    private String province;

    @ExcelProperty("市")
    @ColumnWidth(15)
    private String city;

    @ExcelProperty("区县")
    @ColumnWidth(15)
    private String district;

    @ExcelProperty("电梯位置")
    @ColumnWidth(25)
    private String location;

    @ExcelProperty("所属单位或小区")
    @ColumnWidth(25)
    private String organization;

    @ExcelProperty("负责人姓名")
    @ColumnWidth(25)
    private String managerName;

    @ExcelProperty("负责人职务")
    @ColumnWidth(20)
    private String managerTitle;

    @ExcelProperty("描述")
    @ColumnWidth(30)
    private String description;

    @ExcelProperty(value = "纬度", converter = BigDecimalStringConverter.class)
    @ColumnWidth(20)
    private BigDecimal latitude;

    @ExcelProperty(value = "经度", converter = BigDecimalStringConverter.class)
    @ColumnWidth(20)
    private BigDecimal longitude;

    @ExcelProperty("负责人电话")
    @ColumnWidth(30)
    private String managerPhone;

    @ExcelProperty("备用联系人")
    @ColumnWidth(30)
    private String backupContact;

    @ExcelProperty("备用电话")
    @ColumnWidth(30)
    private String backupPhone;
}