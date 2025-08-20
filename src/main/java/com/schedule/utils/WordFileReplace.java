package com.schedule.utils;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class WordFileReplace {
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
        String inputFilePath = "doc/templete.docx"; // 资源目录中的模板文件

        try {
            // 读取 .docx 文件
            Resource resource = new ClassPathResource(inputFilePath);
            try (InputStream fis = resource.getInputStream();
                 XWPFDocument document = new XWPFDocument(fis);
                 FileOutputStream fos = new FileOutputStream(outputFilePath)) {

                // 遍历所有段落并进行文本替换
                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        String text = run.getText(0);
                        if (text != null) {
                            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                                if (text.contains(entry.getKey())) {
                                    text = text.replace(entry.getKey(), entry.getValue());
                                    run.setText(text, 0);
                                }
                            }
                        }
                    }
                }

                // 保存修改后的文档
                document.write(fos);
                System.out.println("✅ 文档中的文字已成功替换！");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
