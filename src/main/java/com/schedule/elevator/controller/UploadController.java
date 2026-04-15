package com.schedule.elevator.controller;

import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.ParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    private ParamDTO paramDTO;

    @PostMapping
    public BaseResponse upload(@RequestParam("files") MultipartFile[] files,
                                    @RequestParam("path") String savePath) {
        try {
            if (files == null || files.length == 0) {
                throw new IllegalArgumentException("至少上传一个文件");
            }

            // 构造目标目录路径
            Path dirPath = Paths.get(paramDTO.getRootPath() + paramDTO.getScreenshotPath() + savePath); // 自动处理分隔符
            //创建目录
            Files.createDirectories(dirPath);

            StringBuilder fileNames = null;
            System.out.println("---------file list size: " + files.length);
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                // 构造目标文件路径
                // 1. 获取原始文件名，并做安全处理
                String originalFilename = file.getOriginalFilename();
                System.out.println("file name: " + file.getOriginalFilename());
                if (!StringUtils.hasText(originalFilename)) {
                    throw new IllegalArgumentException("文件名不能为空");
                }

                // 安全校验：禁止路径穿越和非法字符
                if (originalFilename.contains("..") || originalFilename.contains("/")) {
                    throw new IllegalArgumentException("文件名不能包含 '..' 或 '/'");
                }
                // 可选：限制扩展名
//                String lowerName = originalFilename.toLowerCase();
//                if (!lowerName.matches("^.+\\.(png|jpg|jpeg|gif|pdf|mp4|mov)$")) {
//                    throw new IllegalArgumentException("仅支持图片、PDF、视频文件");
//                }

//                String fileUrl = paramDTO.getScreenshotPath() + savePath + "/" + originalFilename;
                String fileUrl = UriComponentsBuilder.fromPath(paramDTO.getScreenshotPath())
                        .pathSegment(savePath)
                        .pathSegment(originalFilename)
                        .build()
                        .toUriString();

                // 2. 构建完整物理路径
                Path targetPath = dirPath.resolve(originalFilename);

                // 3. 保存文件（覆盖同名文件）
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                if (fileNames == null) {
                    fileNames = new StringBuilder(fileUrl);
                } else {
                    fileNames.append(",").append(fileUrl);
                }
            }

            return new BaseResponse(HttpStatus.OK.value(), "文件上传成功", fileNames.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "文件上传失败: " + e.getMessage(), null, null);
        }
    }
}
