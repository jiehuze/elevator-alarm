package com.schedule.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.schedule.utils.BigDecimalStringConverter;
import lombok.Data;

import java.math.BigDecimal;

@Data
@HeadRowHeight(30)
@ContentRowHeight(25)
public class MaintenanceTemplateExcel {

    @ExcelProperty("站点名称")
    @ColumnWidth(20)
    private String siteName;

    @ExcelProperty("维保公司")
    @ColumnWidth(25)
    private String maintenanceCompany;

    @ExcelProperty("维保名称")
    @ColumnWidth(20)
    private String maintainerName;

    @ExcelProperty("维保公司电话")
    @ColumnWidth(15)
    private String companyPhone;

    @ExcelProperty("维护人电话")
    @ColumnWidth(30)
    private String maintainerPhone;

    @ExcelProperty("省份")
    @ColumnWidth(20)
    private String province;

    @ExcelProperty("市")
    @ColumnWidth(20)
    private String city;

    @ExcelProperty("区县")
    @ColumnWidth(20)
    private String district;

    @ExcelProperty("维保公司地址")
    @ColumnWidth(30)
    private String companyAddress;

    @ExcelProperty(value = "纬度", converter = BigDecimalStringConverter.class)
    @ColumnWidth(20)
    private BigDecimal latitude;

    @ExcelProperty(value = "经度", converter = BigDecimalStringConverter.class)
    @ColumnWidth(20)
    private BigDecimal longitude;

    @ExcelProperty("维保公司编号")
    @ColumnWidth(30)
    private String companyCode;

    @ExcelProperty("联系人")
    @ColumnWidth(20)
    private String companyManager;

    @ExcelProperty("公司等级")
    @ColumnWidth(20)
    private String companyLevel; // 注意：Excel 中用文字“初级/中级/高级”更友好
}