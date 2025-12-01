package com.schedule.utils;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.math.BigDecimal;

public class BigDecimalStringConverter implements Converter<BigDecimal> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return BigDecimal.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public BigDecimal convertToJavaData(ReadConverterContext<?> context) {
        String cellValue = context.getReadCellData().getStringValue();
        if (StringUtils.isBlank(cellValue)) {
            return null;
        }
        try {
            return new BigDecimal(cellValue.trim());
        } catch (NumberFormatException e) {
            // 如果转换失败，记录日志但返回null，避免整个导入失败
            System.out.println("Failed to convert cell value to BigDecimal: " + cellValue);
            return null;
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<BigDecimal> context) {
        BigDecimal value = context.getValue();
        if (value == null) {
            return new WriteCellData<>("");
        }
        return new WriteCellData<>(value.toString());
    }
}