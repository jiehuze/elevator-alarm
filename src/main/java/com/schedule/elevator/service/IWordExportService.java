package com.schedule.elevator.service;

import com.schedule.elevator.dto.SearchDTO;
import com.schedule.excel.TableData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IWordExportService {
    void generateWordTableToFile(
            String title,
            List<String> headers,
            List<List<String>> dataRows,
            String outputPath) throws IOException;

    void generateWordFromTemplateWithMultipleTables(
            String templatePath,
            Map<String, TableData> tableMap,
            String outputPath) throws IOException;

    void generateMonthlyReport(SearchDTO searchDTO, String outputPath);

    void generateYearlyReport(SearchDTO searchDTO, String outputPath);
}
