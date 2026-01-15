package com.schedule.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelReader {

    public static List<List<String>> readTableFromExcel(String excelPath, int sheetIndex) throws IOException {
        try (FileInputStream fis = new FileInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(sheetIndex);
            return extractTableWithMergedCells(sheet);
        }
    }

    private static List<List<String>> extractTableWithMergedCells(Sheet sheet) {
        DataFormatter formatter = new DataFormatter();
        Map<String, String> mergedValueMap = new HashMap<>();

        // Step 1: 处理所有合并区域
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            int firstRow = region.getFirstRow();
            int firstCol = region.getFirstColumn();

            Row row = sheet.getRow(firstRow);
            Cell cell = row != null ? row.getCell(firstCol) : null;
            String value = (cell != null) ? formatter.formatCellValue(cell) : "";

            for (int r = firstRow; r <= region.getLastRow(); r++) {
                for (int c = firstCol; c <= region.getLastColumn(); c++) {
                    mergedValueMap.put(r + "," + c, value);
                }
            }
        }

        // Step 2: 读取所有非空行
        List<List<String>> table = new ArrayList<>();
        int lastRowNum = sheet.getLastRowNum();
        int firstRowNum = Math.max(0, sheet.getFirstRowNum());

        for (int r = firstRowNum; r <= lastRowNum; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;

            int lastCellNum = row.getLastCellNum();
            if (lastCellNum <= 0) continue;

            List<String> rowData = new ArrayList<>();
            boolean hasContent = false;

            for (int c = 0; c < lastCellNum; c++) {
                String key = r + "," + c;
                String value = mergedValueMap.getOrDefault(key, "");
                if (value.isEmpty()) {
                    Cell cell = row.getCell(c);
                    value = (cell != null) ? formatter.formatCellValue(cell) : "";
                }
                value = value.trim();
                rowData.add(value);
                if (!value.isEmpty()) hasContent = true;
            }

            if (hasContent) {
                table.add(rowData);
            }
        }

        // 对齐列数
        if (!table.isEmpty()) {
            int maxCols = table.stream().mapToInt(List::size).max().orElse(0);
            for (List<String> row : table) {
                while (row.size() < maxCols) {
                    row.add("");
                }
            }
        }

        return table;
    }
}