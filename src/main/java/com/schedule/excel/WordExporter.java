package com.schedule.excel;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class WordExporter {

    /**
     * 将表格数据直接写入到指定的 .docx 文件中
     *
     * @param title      表格标题（可为 null）
     * @param headers    表头列表（按列顺序）
     * @param dataRows   数据行列表（每行是字符串列表）
     * @param outputPath 输出文件路径，例如 "/tmp/电梯明细.docx"
     * @throws IOException 写入文件时可能抛出
     */
    public static void generateWordTableToFile(
            String title,
            List<String> headers,
            List<List<String>> dataRows,
            String outputPath) throws IOException {

        try (XWPFDocument doc = new XWPFDocument()) {

            // 标题
            if (title != null && !title.trim().isEmpty()) {
                XWPFParagraph titlePara = doc.createParagraph();
                titlePara.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun run = titlePara.createRun();
                run.setText(title);
                run.setBold(true);
                run.setFontSize(16);
                run.setFontFamily("微软雅黑");
            }

            // 创建表格
            int rowCount = dataRows.size() + 1; // +1 表头
            int colCount = headers.size();
            XWPFTable table = doc.createTable(rowCount, colCount);

            // 表头
            XWPFTableRow headerRow = table.getRow(0);
            for (int i = 0; i < colCount; i++) {
                setCellTextAndStyle(headerRow.getCell(i), headers.get(i), true);
            }

            // 数据行
            for (int r = 0; r < dataRows.size(); r++) {
                XWPFTableRow row = table.getRow(r + 1);
                List<String> rowData = dataRows.get(r);
                for (int c = 0; c < Math.min(colCount, rowData.size()); c++) {
                    setCellTextAndStyle(row.getCell(c), rowData.get(c), false);
                }
                // 如果某行数据不足，剩余单元格留空（POI 默认已存在）
            }

            // 设置边框
            setTableBorders(table);

            // 写入文件
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                doc.write(out);
            }
        } catch (Exception e) {
            throw new IOException("生成 Word 文档失败: " + outputPath, e);
        }
    }

    /**
     * 基于模板，将多个占位符替换为对应的表格
     *
     * @param templatePath 模板路径（支持 classpath: 或 文件系统路径）
     * @param tableMap     占位符 -> 表格数据的映射，如 {"${elevator_table}", tableData}
     * @param outputPath   输出文件路径
     */
    public static void generateWordFromTemplateWithMultipleTables(String templatePath,
                                                                  Map<String, TableData> tableMap,
                                                                  String outputPath) throws IOException {
        // 加载模板
        InputStream in = new ClassPathResource(templatePath).getInputStream();
        generateWordFromTemplateStreamWithMultipleTables(in, tableMap, outputPath);
    }

    /**
     * 基于模板，将多个占位符替换为对应的表格
     *
     * @param in         模板输入流（支持 classpath: 或 文件系统路径）
     * @param tableMap   占位符 -> 表格数据的映射，如 {"${elevator_table}", tableData}
     * @param outputPath 输出文件路径
     */
    public static void generateWordFromTemplateStreamWithMultipleTables(
            InputStream in,
            Map<String, TableData> tableMap,
            String outputPath) throws IOException {

        XWPFDocument doc = new XWPFDocument(in);
        in.close();

        boolean anyReplaced = false;

        // 遍历段落查找占位符
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            String text = paragraph.getText();
            if (text == null) continue;

            // 检查是否匹配任意占位符
            for (Map.Entry<String, TableData> entry : tableMap.entrySet()) {
                String placeholder = entry.getKey();
                TableData data = entry.getValue();

                if (text.contains(placeholder)) {

                    XWPFTable xwpfTable = insertTable(doc, paragraph, data.getHeaders(), data.getRows());

                    // 清空原始段落内容（删除占位符）
                    paragraph.getRuns().forEach(run -> run.setText("", 0));

                    anyReplaced = true;
                    break; // 一个段落只处理一个占位符
                }
            }
        }

        if (!anyReplaced && !tableMap.isEmpty()) {
            System.out.println("警告：未找到任何占位符，检查模板内容。");
        }

        // 写入输出文件
        File outputFile = new File(outputPath);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }
        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            doc.write(out);
        } finally {
            doc.close();
        }
    }

    private static XWPFTable insertTable(XWPFDocument doc, XWPFParagraph paragraph, java.util.List<String> headers, java.util.List<java.util.List<String>> dataRows) {
        // 创建表格并插入到文档中
        XWPFTable table = doc.insertNewTbl(paragraph.getCTP().newCursor());

        // 设置表格居中对齐
        CTTbl ttbl = table.getCTTbl();
        ttbl.addNewTblPr().addNewJc().setVal(STJcTable.CENTER);

        // 设置表格内容
        int rowCount = dataRows.size() + 1;
        int colCount = headers.size();
        // 使用 cursor 创建表格并插入到指定位置

        // 设置表头
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < colCount; i++) {
            if (headerRow.getCell(i) == null) {
                headerRow.addNewTableCell();
            }
            setCellTextAndStyle(headerRow.getCell(i), headers.get(i), true);
            // 获取该行的底层 XML 对象
//            var cTr = headerRow.getCtRow();
            // 设置 <w:tblHeader /> 属性
//            cTr.setTblHeader(true); // 这个空元素表示“此行为表头”
        }

        // 设置数据行
        System.out.println("rowCount: " + rowCount);
        for (int r = 0; r < dataRows.size(); r++) {
            List<String> rowData = dataRows.get(r);
            XWPFTableRow row;
//            System.out.println("r: " + r);
//            System.out.println("row: " + table.getNumberOfRows());
            if (r < table.getNumberOfRows() - 1) {
                row = table.getRow(r + 1);
            } else {
//                System.out.println("创建新行");
                row = table.createRow(); // 创建新行
//              table.addRow(row); // 添加到表格
            }
            for (int c = 0; c < Math.min(colCount, rowData.size()); c++) {
                if (row.getCell(c) == null) {
                    row.addNewTableCell();
                }
                setCellTextAndStyle(row.getCell(c), rowData.get(c), false);
            }
        }

        setTableBorders(table);

        return table;
    }

    // --- 表格创建与样式 ---
    private static XWPFTable createTable(XWPFDocument doc, java.util.List<String> headers, java.util.List<java.util.List<String>> dataRows) {
        int rowCount = dataRows.size() + 1;
        int colCount = headers.size();
        XWPFTable table = doc.createTable(rowCount, colCount);

        for (int i = 0; i < colCount; i++) {
            setCellTextAndStyle(table.getRow(0).getCell(i), headers.get(i), true);
        }
        for (int r = 0; r < dataRows.size(); r++) {
            java.util.List<String> row = dataRows.get(r);
            for (int c = 0; c < Math.min(colCount, row.size()); c++) {
                setCellTextAndStyle(table.getRow(r + 1).getCell(c), row.get(c), false);
            }
        }
        setTableBorders(table);
        return table;
    }

    private static void setCellTextAndStyle(XWPFTableCell cell, String text, boolean isHeader) {
        // 直接设置单元格文本，而不是操作段落
        cell.setText(text != null ? text : "");

        // 获取段落并设置样式
        XWPFParagraph p = cell.getParagraphs().get(0);
        p.setAlignment(ParagraphAlignment.CENTER);

        // 设置run的样式
        if (!p.getRuns().isEmpty()) {
            XWPFRun run = p.getRuns().get(0);
            run.setFontFamily("微软雅黑");
            run.setFontSize(isHeader ? 8 : 7);
            if (isHeader) {
                run.setBold(true);
            }
        }

        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
    }

    private static void setCellText(XWPFTableCell cell, String text, boolean isHeader) {
        cell.getParagraphs().clear();
        XWPFParagraph p = cell.addParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = p.createRun();
        run.setText(text == null ? "" : text);
        run.setFontFamily("微软雅黑");
        run.setFontSize(isHeader ? 8 : 7);
        if (isHeader) {
            run.setBold(true);
            // 可选：设置灰色背景
            cell.setColor("D9E2F3"); // 浅蓝色
        }
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
    }

    private static void setTableBorders(XWPFTable table) {
        CTBorder border = CTBorder.Factory.newInstance();
        border.setColor("auto");
        border.setSz(new BigInteger("4"));
        border.setVal(STBorder.SINGLE);

        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                var tcPr = cell.getCTTc().addNewTcPr();
                var borders = tcPr.addNewTcBorders();
                borders.setTop(border);
                borders.setBottom(border);
                borders.setLeft(border);
                borders.setRight(border);
            }
        }
    }

    private static XWPFTable createComplexTable(XWPFDocument doc, List<List<String>> tableData) {
        int rows = tableData.size();
        int cols = tableData.get(0).size();

        XWPFTable table = doc.createTable(rows, cols);
        table.setWidth("100%");

        // 填充内容并设置样式
        for (int r = 0; r < rows; r++) {
            XWPFTableRow row = table.getRow(r);
            for (int c = 0; c < cols; c++) {
                setCellText(row.getCell(c), tableData.get(r).get(c), r == 0);
            }
        }

        // === 手动合并单元格 ===
        // 根据你的表格逻辑：
        // - 序号=1 跨 6 行（第0～5行）
        // - 类型=人为原因 跨 6 行
        // - 小计(%)=40.95% 跨 6 行
        // - 序号=2 跨 5 行（第6～10行）
        // - 类型=外部原因 跨 5 行
        // - 小计(%)=14.96% 跨 5 行

        mergeVertically(table, 0, 0, 5); // 序号列
        mergeVertically(table, 0, 1, 5); // 类型列
        mergeVertically(table, 0, 6, 5); // 小计列

        mergeVertically(table, 6, 0, 10);
        mergeVertically(table, 6, 1, 10);
        mergeVertically(table, 6, 6, 10);

        setTableBorders(table);
        return table;
    }

    private static void mergeVertically(XWPFTable table, int startRow, int col, int endRow) {
        // 合并从 startRow 到 endRow 的同一列
        XWPFTableCell firstCell = table.getRow(startRow).getCell(col);
        for (int r = startRow + 1; r <= endRow; r++) {
            XWPFTableCell cell = table.getRow(r).getCell(col);
            // 清空内容（由首单元格显示）
            cell.setText("");
            // 设置 vertical merge
            var tcPr = cell.getCTTc().addNewTcPr();
            tcPr.addNewVMerge().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge.CONTINUE);
        }
        // 首单元格设置 restart
        var firstTcPr = firstCell.getCTTc().addNewTcPr();
        firstTcPr.addNewVMerge().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge.RESTART);
    }

}