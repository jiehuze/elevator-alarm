package com.schedule.elevator.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.ElevatorInfoDTO;
import com.schedule.excel.ElevatorInfoExcelConverter;
import com.schedule.excel.ElevatorInfoTemplateExcel;
import com.schedule.elevator.entity.ElevatorInfo;
import com.schedule.elevator.service.IElevatorInfoService;
import com.schedule.utils.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static com.schedule.utils.ExcelUtil.isExcelFile;

@RestController
@RequestMapping("/info")
public class ElevatorInfoController {

    @Autowired
    private IElevatorInfoService elevatorInfoService;

    @PostMapping("/add")
    public BaseResponse create(@RequestBody ElevatorInfo elevator) {
        elevatorInfoService.save(elevator);
        return new BaseResponse(HttpStatus.OK.value(), "添加成功", elevator, null);
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        elevatorInfoService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "删除成功", null, null);
    }

    @PutMapping
    public BaseResponse update(@RequestBody ElevatorInfo elevator) {
        elevatorInfoService.updateById(elevator);
        return new BaseResponse(HttpStatus.OK.value(), "更新成功", elevator, null);
    }

    @GetMapping("/{id}")
    public BaseResponse get(@PathVariable Long id) {
        ElevatorInfo elevator = elevatorInfoService.getById(id);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", elevator, null);
    }

    @GetMapping("/list")
    public BaseResponse list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute ElevatorInfoDTO elevatorInfoDTO) {
        Page<ElevatorInfo> page = new Page<>(current, size);
        IPage<ElevatorInfo> result = elevatorInfoService.pageElevators(page, elevatorInfoDTO);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", result, null);
    }

    @GetMapping("/export")
    public void exportElevators(HttpServletResponse response) throws Exception {
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("电梯信息列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");


        List<ElevatorInfo> list = elevatorInfoService.list(); // 从数据库查所有

        // 转换为 Excel DTO
        List<ElevatorInfoTemplateExcel> dtoList = list.stream()
                .map(ElevatorInfoExcelConverter::toDto)
                .collect(Collectors.toList());

        System.out.println("list size:" + dtoList.toString());

        // 写入 Excel
        ExcelUtil.exportExcelToTargetWithTemplate(response, null, "电梯信息", dtoList, ElevatorInfoTemplateExcel.class, "doc/elevator.xlsx");
    }

    @PostMapping("/import")
    public BaseResponse importElevators(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 校验是否为 Excel 文件（可选）
            if (!isExcelFile(file)) {
                return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "请上传 .xls 或 .xlsx 文件", null, null);
            }

            // 2. 导入解析
            List<ElevatorInfoTemplateExcel> dtoList = ExcelUtil.importExcel(file, ElevatorInfoTemplateExcel.class);

            System.out.println("dtoList size:" + dtoList.size());
            // 3. 转换为实体并保存（注意空值处理）
            List<ElevatorInfo> entities = dtoList.stream()
                    .map(ElevatorInfoExcelConverter::toEntity) // 使用你已有的静态方法
                    .filter(e -> e != null &&
                            StringUtils.isNotBlank(e.getElevatorNo()) &&
                            !"电梯编号".equals(e.getElevatorNo().trim()))
                    .collect(Collectors.toList());

            // 4. 批量保存（根据业务决定是否去重、校验等）
            elevatorInfoService.saveBatch(entities);

            return new BaseResponse(HttpStatus.OK.value(), "成功导入 " + entities.size() + " 条电梯信息", null, null);
        } catch (Exception e) {
            System.out.println("Excel 导入失败:" + e);
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "导入失败: " + e.getMessage(), null, null);
        }
    }
}