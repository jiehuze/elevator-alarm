package com.schedule.elevator.service.impl;

import com.schedule.elevator.service.IWordExportService;
import com.schedule.excel.TableData;
import com.schedule.excel.WordExporter;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class WordExportServiceImpl implements IWordExportService {
    @Override
    /**
     * 将表格数据直接写入到指定的 .docx 文件中
     *
     * @param title       表格标题（可为 null）
     * @param headers     表头列表（按列顺序）
     * @param dataRows    数据行列表（每行是字符串列表）
     * @param outputPath  输出文件路径，例如 "/tmp/电梯明细.docx"
     * @throws IOException 写入文件时可能抛出
     */
    public void generateWordTableToFile(
            String title,
            List<String> headers,
            List<List<String>> dataRows,
            String outputPath) throws IOException {

        WordExporter.generateWordTableToFile(title, headers, dataRows, outputPath);
    }

    /**
     * 基于模板，将多个占位符替换为对应的表格
     *
     * @param templatePath 模板路径（支持 classpath: 或 文件系统路径）
     * @param tableMap     占位符 -> 表格数据的映射，如 {"${elevator_table}", tableData}
     * @param outputPath   输出文件路径
     */
    public void generateWordFromTemplateWithMultipleTables(
            String templatePath,
            Map<String, TableData> tableMap,
            String outputPath) throws IOException {
        WordExporter.generateWordFromTemplateWithMultipleTables(templatePath, tableMap, outputPath);
    }
}
