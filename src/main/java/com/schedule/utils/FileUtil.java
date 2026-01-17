package com.schedule.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    /**
     * 确保指定目录存在，如果不存在则创建
     *
     * @param directoryPath 目录路径
     * @return 创建是否成功
     */
    /**
     * 确保文件所在目录存在，若不存在则创建（包括所有父级目录）
     *
     * @param filePath 文件的完整路径（如 /a/b/c/file.txt）
     */
    public static void ensureDirectoryExists(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        // 获取文件所在目录（去掉文件名）
        Path file = Paths.get(filePath);
        Path directory = file.getParent();

        if (directory != null && !Files.exists(directory)) {
            try {
                Files.createDirectories(directory); // 递归创建所有缺失的父目录
                System.out.println("目录已创建: " + directory);
            } catch (IOException e) {
                throw new RuntimeException("无法创建目录: " + directory, e);
            }
        } else {
            System.out.println("目录已存在: " + directory);
        }
    }

    public static long getFileSizeInKB(String filePath) {
        try {
            Path path = Path.of(filePath);

            if (!Files.exists(path)) {
                return -1; // 文件不存在
            }

            long fileSizeBytes = Files.size(path);
            // 转换为KB，向上取整
            long fileSizeKB = (long) Math.ceil((double) fileSizeBytes / 1024);
            return fileSizeKB;
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // 读取失败
        }
    }

    public static void main(String[] args) {
        String testPath = "file.docx";
        FileUtil.ensureDirectoryExists(testPath);
        System.out.println("创建目录：");
    }
}
