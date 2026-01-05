package com.schedule.utils;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class FileReplace {
    public static boolean replace(String oldText, String newText, String outputFilePath) {
        String inputFilePath = "doc/templete.doc";  // 原文档路径
//        String outputFilePath = "/Users/jiehu/works/test/replacefile/testreplace.doc";  // 替换后的文档路径

        try {
            // 打开 .doc 文件
            Resource resource = new ClassPathResource(inputFilePath);
            InputStream fis = resource.getInputStream();
            HWPFDocument document = new HWPFDocument(fis);

            // 获取文件的范围（即文档的所有文本内容）
            Range range = document.getRange();

            // 遍历文档中的每个字符块（CharacterRun）
            for (int i = 0; i < range.numCharacterRuns(); i++) {
                CharacterRun run = range.getCharacterRun(i);
                String text = run.text();
                if (text.contains(oldText)) {
                    // 替换文本并保持格式
                    text = text.replace(oldText, newText);
                    run.replaceText(run.text(), text);  // 更新文本
                }
            }

            // 保存修改后的文档
            FileOutputStream fos = new FileOutputStream(outputFilePath);
            document.write(fos);
            fos.close();
            fis.close();

            System.out.println("文档中的文字已成功替换！");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean replaceTextInExcel(Map<String, String> replacements, String outputFilePath) {
        String inputFilePath = "doc/templete.xlsx"; // 模板在 resources/excel/ 下

        try {
            Resource resource = new ClassPathResource(inputFilePath);
            try (InputStream fis = resource.getInputStream();
                 Workbook workbook = new XSSFWorkbook(fis);
                 FileOutputStream fos = new FileOutputStream(outputFilePath)) {

                // 遍历所有工作表
                for (Sheet sheet : workbook) {
                    // 遍历所有行
                    for (Row row : sheet) {
                        // 遍历所有单元格
                        for (Cell cell : row) {
                            if (cell.getCellType() == CellType.STRING) {
                                String text = cell.getStringCellValue();
                                if (text != null && text.contains("{{")) {
                                    // 执行替换
                                    String newText = text;
                                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                                        // 精确匹配占位符（要求模板中无多余空格）
                                        if (newText.contains(entry.getKey())) {
                                            newText = newText.replace(entry.getKey(), entry.getValue());
                                        }
                                    }
                                    if (!newText.equals(text)) {
                                        cell.setCellValue(newText);
                                    }
                                }
                            }
                        }
                    }
                }

                workbook.write(fos);
                System.out.println("✅ Excel 模板占位符替换成功！");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 替换 Word 文档中的多个字符串
     *
     * @param replacements   需要替换的文本映射（key: 旧文本，value: 新文本）
     * @param inputFilePath  输入文件（相对 resources 目录）
     * @param outputFilePath 输出文件路径
     * @return 替换成功返回 true，失败返回 false
     */
    public static boolean replaceTextInWord(Map<String, String> replacements, String outputFilePath) {
        String inputFilePath = "doc/templete.doc";
        try {
            // 读取 .doc 文件
            Resource resource = new ClassPathResource(inputFilePath);
            try (InputStream fis = resource.getInputStream();
                 HWPFDocument document = new HWPFDocument(fis);
                 FileOutputStream fos = new FileOutputStream(outputFilePath)) {

                // 获取文件的范围（即文档的所有文本内容）
                Range range = document.getRange();

                // 遍历文档中的每个字符块（CharacterRun）
                for (int i = 0; i < range.numCharacterRuns(); i++) {
                    CharacterRun run = range.getCharacterRun(i);
                    String text = run.text();

                    // 遍历需要替换的所有字符串
                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                        if (text.contains(entry.getKey())) {
                            // 替换文本但保持原格式
                            text = text.replace(entry.getKey(), entry.getValue());
                            run.replaceText(run.text(), text);
                        }
                    }
                }

                // 保存修改后的文档
                document.write(fos);

                System.out.println("文档中的文字已成功替换！");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 替换 Word 文档 (.docx) 中的多个字符串
     *
     * @param replacements   需要替换的文本映射（key: 旧文本，value: 新文本）
     * @param outputFilePath 输出文件路径
     * @return 替换成功返回 true，失败返回 false
     */
    public static boolean replaceTextInWordX(Map<String, String> replacements, String outputFilePath) {
        String inputFilePath = "doc/templete.docx";

        try {
            Resource resource = new ClassPathResource(inputFilePath);
            try (InputStream fis = resource.getInputStream();
                 XWPFDocument document = new XWPFDocument(fis);
                 FileOutputStream fos = new FileOutputStream(outputFilePath)) {

                // 替换正文段落
                replaceInParagraphs(document.getParagraphs(), replacements);

                // 替换表格中的段落
                for (XWPFTable table : document.getTables()) {
                    for (XWPFTableRow row : table.getRows()) {
                        for (XWPFTableCell cell : row.getTableCells()) {
                            replaceInParagraphs(cell.getParagraphs(), replacements);
                        }
                    }
                }

                document.write(fos);
                System.out.println("✅ 文档中的文字（包括表格）已成功替换！");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 抽取公共方法：替换一组段落中的占位符
    private static void replaceInParagraphs(List<XWPFParagraph> paragraphs, Map<String, String> replacements) {
        for (XWPFParagraph paragraph : paragraphs) {
            for (XWPFRun run : paragraph.getRuns()) {
                if (run != null) {
                    String text = run.getText(0);
                    if (text != null) {
                        // 🔍 调试日志：打印所有包含 {{ 的文本
                        if (text.contains("{{")) {
                            System.out.println("🔍 Found placeholder in run: '" + text + "'");
                        }

                        // 执行替换
                        for (Map.Entry<String, String> entry : replacements.entrySet()) {
                            if (text.contains(entry.getKey())) {
                                text = text.replace(entry.getKey(), entry.getValue());
                                run.setText(text, 0);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
//        WordFileReplace.replace("X", "CCCCC");
        Map<String, String> replacements = Map.of(
                "{{what}}", "20250128督办会议",
                "{{Y}}", "2025",
                "{{M}}", "01",
                "{{D}}", "11"
        );

        // 输出文件路径
        String outputFilePath = "/Users/jiehu/works/test/replacefile/output.docx";

        // 调用方法进行替换
        boolean success = replaceTextInWordX(replacements, outputFilePath);

        if (success) {
            System.out.println("Word 文件替换成功，已保存至: " + outputFilePath);
        } else {
            System.out.println("Word 文件替换失败！");
        }
    }
}
