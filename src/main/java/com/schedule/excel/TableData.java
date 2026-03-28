package com.schedule.excel;

import com.schedule.utils.ExcelUtil;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Data
public class TableData {
    private List<String> headers;
    private List<List<String>> rows;

    public TableData(List<String> headers, List<List<String>> rows) {
        this.headers = headers;
        this.rows = rows;
    }

    public static Map<String, TableData> buildTableData(Map<String, TableData> tableMap, List<?> dataList, Class<?> clazz, String replaceKey) {
        List<String> headers = ExcelUtil.extractHeaders(clazz);
        List<List<String>> rows = ExcelUtil.extractDataList(dataList, clazz);
        tableMap.put(replaceKey, new TableData(headers, rows));
        return tableMap;
    }

    public static <T> Map<String, TableData> buildTableData(Map<String, TableData> tableMap, T data, Class<T> clazz, String replaceKey) {
        List<T> dataList = Arrays.asList(data);
        List<String> headers = ExcelUtil.extractHeaders(clazz);
        List<List<String>> rows = ExcelUtil.extractDataList(dataList, clazz);
        tableMap.put(replaceKey, new TableData(headers, rows));
        return tableMap;
    }
}
