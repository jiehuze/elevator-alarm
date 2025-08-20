package com.schedule.utils;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.schedule.excel.FormTemplateExcel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

public class CustomMergeStrategy extends AbstractMergeStrategy {

    private final List<FormTemplateExcel> dataList;
    private int startRow = 1; // 跳过表头，从第一行开始

    public CustomMergeStrategy(List<FormTemplateExcel> dataList) {
        this.dataList = dataList;
    }

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        int columnIndex = cell.getColumnIndex();

        // 仅合并第一列（type）和第二列（predictedIndicator）
        if (columnIndex > 1) {
            return;
        }

        // 避免重复合并
        if (cell.getRowIndex() != startRow) {
            return;
        }

        int rowCount = dataList.size();
        int mergeRowCount = 1;

        // 计算相同 "报表" (type) 的合并行数
        for (int i = relativeRowIndex + 1; i < rowCount; i++) {
            if (dataList.get(i).getType().equals(dataList.get(relativeRowIndex).getType())) {
                mergeRowCount++;
            } else {
                break;
            }
        }

        // 进行合并
        if (mergeRowCount > 1) {
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + mergeRowCount - 1, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + mergeRowCount - 1, 1, 1));
        }

        // 更新起始行
        startRow += mergeRowCount;
    }
}
