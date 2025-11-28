package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.ParamDTO;
import com.schedule.elevator.entity.WorkOrderScreenshot;
import com.schedule.elevator.service.IWorkOrderScreenshotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "工单截图管理")
@RestController
@RequestMapping("/screenshots")
public class WorkOrderScreenshotController {

    @Autowired
    private IWorkOrderScreenshotService screenshotService;

    @Autowired
    private ParamDTO paramDTO;

    @ApiOperation("根据工单ID查询所有截图")
    @GetMapping("/{orderNo}")
    public List<WorkOrderScreenshot> listByWorkOrder(@PathVariable String orderNo) {
        return screenshotService.list(
                new QueryWrapper<WorkOrderScreenshot>()
                        .eq("order_no", orderNo)
                        .eq("is_deleted", 0)
                        .orderByDesc("upload_time")
        );
    }

    @ApiOperation("保存新截图记录（注意：文件需先上传到存储服务）")
    @PostMapping
    public WorkOrderScreenshot save(@RequestBody WorkOrderScreenshot screenshot) {
        screenshotService.save(screenshot);
        return screenshot;
    }

    @ApiOperation("根据工单号删除所有截图（逻辑删除）")
    @DeleteMapping("/by-order-no/{orderNo}")
    public void deleteByOrderNo(@ApiParam(value = "工单号", required = true)
                                @PathVariable String orderNo) {
        // 构造更新条件：order_no = ? 且 is_deleted = 0（避免重复删）
        WorkOrderScreenshot updateEntity = new WorkOrderScreenshot();
        updateEntity.setDeleted(1); // 标记为已删除

        screenshotService.update(updateEntity,
                new LambdaUpdateWrapper<WorkOrderScreenshot>()
                        .eq(WorkOrderScreenshot::getOrderNo, orderNo)
                        .eq(WorkOrderScreenshot::getDeleted, 0)
        );
    }

    // ========== 新增：批量上传工单截图 ==========

    @ApiOperation("上传工单截图（支持多文件）")
    @PostMapping("/upload")
    public BaseResponse uploadScreenshots(
            @ModelAttribute @NotNull WorkOrderScreenshot workOrderScreenshot,
            @ApiParam(value = "截图文件（可多选）", required = true)
            @RequestParam("files") MultipartFile[] files) {

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("至少上传一个文件");
        }

        //创建路径
        Path dirPath = Paths.get(paramDTO.getUploadPath(), workOrderScreenshot.getOrderNo()); // 自动处理分隔符
        try {
            Files.createDirectories(dirPath);
        } catch (IOException e) {
            throw new RuntimeException("创建目录失败: " + dirPath.toString(), e);
        }

        List<WorkOrderScreenshot> entitiesToSave = new ArrayList<>();
        StringBuilder fileNames = null;
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            // 1. 获取原始文件名，并做安全处理
            String originalFilename = file.getOriginalFilename();
            if (!StringUtils.hasText(originalFilename)) {
                throw new IllegalArgumentException("文件名不能为空");
            }

            // 安全校验：禁止路径穿越和非法字符
            if (originalFilename.contains("..") || originalFilename.contains("/")) {
                throw new IllegalArgumentException("文件名不能包含 '..' 或 '/'");
            }
            // 可选：限制扩展名
            String lowerName = originalFilename.toLowerCase();
            if (!lowerName.matches("^.+\\.(png|jpg|jpeg|gif|pdf|mp4|mov)$")) {
                throw new IllegalArgumentException("仅支持图片、PDF、视频文件");
            }

            // 2. 构建完整物理路径
            Path targetPath = dirPath.resolve(originalFilename);

            // 3. 保存文件（覆盖同名文件）
            try {
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("文件写入失败: " + targetPath, e);
            }

            if (fileNames == null) {
                fileNames = new StringBuilder(originalFilename);
            } else {
                fileNames.append(",").append(originalFilename);
            }
        }

        workOrderScreenshot.setFileNames(fileNames.toString())
                .setFilePath(Paths.get(paramDTO.getDownloadPath(), workOrderScreenshot.getOrderNo()).toString()) // 供 Nginx 访问
                .setUploaderName(workOrderScreenshot.getUploaderName())
                .setUploadTime(LocalDateTime.now())
                .setContentType("jpg");

        // 批量保存到数据库
        screenshotService.save(workOrderScreenshot);

        return new BaseResponse(200, "上传成功", workOrderScreenshot, null);
    }
}