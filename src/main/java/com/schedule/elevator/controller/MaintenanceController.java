package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.NearbyMaintenanceDTO;
import com.schedule.elevator.entity.ElevatorInfo;
import com.schedule.elevator.entity.Maintenance;
import com.schedule.elevator.service.IMaintenanceService;
import com.schedule.excel.ElevatorInfoExcelConverter;
import com.schedule.excel.ElevatorInfoTemplateExcel;
import com.schedule.excel.MaintenanceExcelConverter;
import com.schedule.excel.MaintenanceTemplateExcel;
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
@RequestMapping("/maintenance")
public class MaintenanceController {

    @Autowired
    private IMaintenanceService maintenanceInfoService;

    @PostMapping("/add")
    public BaseResponse add(@RequestBody Maintenance maintenance) {
        maintenanceInfoService.save(maintenance);
        return new BaseResponse(HttpStatus.OK.value(), "维保信息添加成功", maintenance, null);
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        maintenanceInfoService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "维保信息删除成功", null, null);
    }

    @PutMapping("/update")
    public BaseResponse update(@RequestBody Maintenance maintenance) {
        maintenanceInfoService.updateById(maintenance);
        return new BaseResponse(HttpStatus.OK.value(), "维保信息更新成功", maintenance, null);
    }

    @GetMapping("/{id}")
    public BaseResponse get(@PathVariable Long id) {
        Maintenance info = maintenanceInfoService.getById(id);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", info, null);
    }

    @GetMapping("/list")
    public BaseResponse list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        Page<Maintenance> page = new Page<>(current, size);
        IPage<Maintenance> result = maintenanceInfoService.page(page);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", result, null);
    }

    @GetMapping("/nearby")
    public BaseResponse getNearby(@ModelAttribute NearbyMaintenanceDTO nearbyMaintenanceDTO) {
        List<NearbyMaintenanceDTO> nearby = maintenanceInfoService.getNearby(
                nearbyMaintenanceDTO.getLatitude(),
                nearbyMaintenanceDTO.getLongitude(),
                nearbyMaintenanceDTO.getDistanceKm());
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", nearby, null);
    }

    @GetMapping("/export")
    public void exportElevators(HttpServletResponse response) throws Exception {
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("维保信息列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");


        List<Maintenance> list = maintenanceInfoService.list(); // 从数据库查所有

        // 转换为 Excel DTO
        List<MaintenanceTemplateExcel> dtoList = list.stream()
                .map(MaintenanceExcelConverter::toDto)
                .collect(Collectors.toList());

        System.out.println("list size:" + dtoList.toString());

        // 写入 Excel
        ExcelUtil.exportExcelToTargetWithTemplate(response, null, "维保信息", dtoList, MaintenanceTemplateExcel.class, "doc/maintenance.xlsx");
    }

    @PostMapping("/import")
    public BaseResponse importElevators(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 校验是否为 Excel 文件（可选）
            if (!isExcelFile(file)) {
                return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "请上传 .xls 或 .xlsx 文件", null, null);
            }

            // 2. 导入解析
            List<MaintenanceTemplateExcel> dtoList = ExcelUtil.importExcel(file, MaintenanceTemplateExcel.class);

            System.out.println("dtoList size:" + dtoList.size());
            // 3. 转换为实体并保存（注意空值处理）
            List<Maintenance> entities = dtoList.stream()
                    .map(MaintenanceExcelConverter::toEntity) // 使用你已有的静态方法
                    .filter(e -> e != null &&
//                            StringUtils.isNotBlank(e.getElevatorNo()) &&
                            !"电梯编号".equals(e.getMaintainerName().trim()))
                    .collect(Collectors.toList());

            // 4. 批量保存（根据业务决定是否去重、校验等）
            maintenanceInfoService.saveBatch(entities);

            return new BaseResponse(HttpStatus.OK.value(), "成功导入 " + entities.size() + " 条维保信息", null, null);
        } catch (Exception e) {
            System.out.println("Excel 导入失败:" + e);
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "导入失败: " + e.getMessage(), null, null);
        }
    }
}