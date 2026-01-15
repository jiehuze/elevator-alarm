package com.schedule.excel;

import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 直接操作 .docx 文档的占位符替换工具类（无需转 XML）
 * 支持：文本占位符、表格内占位符、选择性替换
 */
public class DocxPlaceholderReplaceUtil {

    // 匹配 ${xxx} 格式的占位符正则（支持换行、空格等特殊字符）
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    /**
     * 重载方法：返回InputStream（用于Spring Boot前端下载，无需本地保存）
     */
    public static InputStream replacePlaceholderToStream(InputStream inputStream, Map<String, String> replaceMap) throws Exception {
        XWPFDocument doc = new XWPFDocument(inputStream);

        // 替换占位符
        replaceParagraphs(doc.getParagraphs(), replaceMap);
        replaceTables(doc.getTables(), replaceMap);

        // 写入字节数组输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doc.write(outputStream);

        // 关闭流（保留ByteArrayInputStream供前端使用）
        doc.close();
        inputStream.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }
    /**
     * 替换 .docx 模板中的占位符（选择性替换）
     *
     * @param templatePath 模板文件路径（如：D:/template/month.docx）
     * @param replaceMap   要替换的占位符Map（key=占位符名称，如 "DistrictS"；value=替换值，如 "承德市"）
     * @param outputPath   输出文件路径（如：D:/output/电梯统计报告202401.docx）
     * @throws Exception 异常（文件读取/写入失败）
     */
    public static void replacePlaceholder(String templatePath, Map<String, String> replaceMap, String outputPath) throws Exception {
        // 1. 读取模板文件
        File templateFile = new File(templatePath);
        if (!templateFile.exists()) {
            throw new FileNotFoundException("模板文件不存在：" + templatePath);
        }
        InputStream inputStream = new FileInputStream(templateFile);
        XWPFDocument doc = new XWPFDocument(inputStream);

        // 2. 替换「段落中的占位符」（文档正文文本）
        replaceParagraphs(doc.getParagraphs(), replaceMap);

        // 3. 替换「表格中的占位符」（所有表格的单元格文本）
        replaceTables(doc.getTables(), replaceMap);

        // 4. 写入新文件
        OutputStream outputStream = new FileOutputStream(outputPath);
        doc.write(outputStream);

        // 5. 关闭流
        outputStream.close();
        doc.close();
        inputStream.close();
    }

    /**
     * 替换段落中的占位符
     */
    private static void replaceParagraphs(List<XWPFParagraph> paragraphs, Map<String, String> replaceMap) {
        for (XWPFParagraph paragraph : paragraphs) {
            // 遍历段落中的所有文本块（避免丢失格式）
            List<XWPFRun> runs = paragraph.getRuns();
            StringBuilder fullText = new StringBuilder();
            for (XWPFRun run : runs) {
                fullText.append(run.getText(0));
            }

            // 匹配并替换占位符
            String replacedText = replacePlaceholders(fullText.toString(), replaceMap);

            // 重新写入替换后的文本（保留原格式）
            if (!replacedText.equals(fullText.toString())) {
                // 清空原有文本块
                for (XWPFRun run : runs) {
                    run.setText("", 0);
                }
                // 写入替换后的文本（保持原段落格式）
                if (!replacedText.isEmpty()) {
                    runs.get(0).setText(replacedText, 0);
                }
            }
        }
    }

    /**
     * 替换表格中的占位符（遍历所有单元格）
     */
    private static void replaceTables(List<XWPFTable> tables, Map<String, String> replaceMap) {
        for (XWPFTable table : tables) {
            // 遍历表格行
            for (XWPFTableRow row : table.getRows()) {
                // 遍历行中的单元格
                for (XWPFTableCell cell : row.getTableCells()) {
                    // 替换单元格内的段落文本
                    replaceParagraphs(cell.getParagraphs(), replaceMap);
                }
            }
        }
    }

    /**
     * 核心替换逻辑：匹配 ${xxx} 占位符，仅替换 replaceMap 中存在的key
     */
    private static String replacePlaceholders(String text, Map<String, String> replaceMap) {
        if (text == null || text.isEmpty() || replaceMap.isEmpty()) {
            return text;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        int lastEnd = 0;

        // 遍历所有匹配的占位符
        while (matcher.find()) {
            String placeholder = matcher.group(0); // 完整占位符（如 ${DistrictS}）
            String key = matcher.group(1); // 占位符名称（如 DistrictS）

            // 若 replaceMap 包含该key，则替换；否则保留原占位符
            if (replaceMap.containsKey(key)) {
                sb.append(text.substring(lastEnd, matcher.start()));
                sb.append(replaceMap.get(key)); // 替换为目标值
            } else {
                sb.append(text.substring(lastEnd, matcher.end())); // 保留原占位符
            }
            lastEnd = matcher.end();
        }

        // 拼接剩余文本
        sb.append(text.substring(lastEnd));
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
// 2. 选择要替换的占位符（动态从数据库查询，这里模拟）
        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("DistrictS", "承德市");
        replaceMap.put("StartTime", "2024-01-01");
        replaceMap.put("EndTime", "2024-01-31");
        replaceMap.put("total", "420");
        replaceMap.put("0101", "25");
        replaceMap.put("01011", "5.95");

        DocxPlaceholderReplaceUtil.replacePlaceholder("/Users/jiehu/works/lavie/elevator-alarm/src/main/resources/doc/month.docx", replaceMap, "test4.docx");

    }
}