package com.schedule.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import lombok.Data;

@Data
@ContentRowHeight(40)
@HeadRowHeight(40)
@ColumnWidth(40)
public class FormTemplateExcel {

    @ExcelProperty(value = "报表", index = 0)
    @ContentStyle(wrapped = BooleanEnum.TRUE,
            horizontalAlignment = HorizontalAlignmentEnum.CENTER,  // 水平居中
            verticalAlignment = VerticalAlignmentEnum.CENTER       // 垂直居中
    )
    private String type;
    @ExcelProperty(value = "报表预估指标", index = 1)
    @ContentStyle(wrapped = BooleanEnum.TRUE,
            horizontalAlignment = HorizontalAlignmentEnum.CENTER,  // 水平居中
            verticalAlignment = VerticalAlignmentEnum.CENTER       // 垂直居中
    )
    private String predictedGear;
    @ExcelProperty(value = "指标名称", index = 2)
    @ContentStyle(wrapped = BooleanEnum.TRUE,
            horizontalAlignment = HorizontalAlignmentEnum.CENTER,  // 水平居中
            verticalAlignment = VerticalAlignmentEnum.CENTER       // 垂直居中
    )
    private String name;
    @ExcelProperty(value = "责任单位", index = 3)
    private String dept;
    @ExcelProperty(value = "指标预估档位", index = 4)
    @ContentStyle(wrapped = BooleanEnum.TRUE,
            horizontalAlignment = HorizontalAlignmentEnum.CENTER,  // 水平居中
            verticalAlignment = VerticalAlignmentEnum.CENTER       // 垂直居中
    )
    private String predictedGearTarget;
    @ExcelProperty(value = "指标实际档位", index = 5)
    @ContentStyle(wrapped = BooleanEnum.TRUE,
            horizontalAlignment = HorizontalAlignmentEnum.CENTER,  // 水平居中
            verticalAlignment = VerticalAlignmentEnum.CENTER       // 垂直居中
    )
    private String actualGearTarget;
    @ExcelProperty(value = "工作进展", index = 6)
    @ContentStyle(wrapped = BooleanEnum.TRUE)
    private String workProgress;
    @ExcelProperty(value = "存在问题", index = 7)
    @ContentStyle(wrapped = BooleanEnum.TRUE)
    private String issues;
}
