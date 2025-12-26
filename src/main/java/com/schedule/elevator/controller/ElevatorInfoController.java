package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.ElevatorInfoDTO;
import com.schedule.elevator.dto.MaintenanceDTO;
import com.schedule.elevator.entity.Community;
import com.schedule.elevator.entity.ElevatorInfo;
import com.schedule.elevator.entity.PropertyInfo;
import com.schedule.elevator.service.*;
import com.schedule.excel.ElevatorImportExcelConverter;
import com.schedule.excel.ElevatorImportTemplateExcel;
import com.schedule.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.schedule.utils.ExcelUtil.isExcelFile;

@RestController
@RequestMapping("/info")
public class ElevatorInfoController {

    @Autowired
    private IElevatorInfoService elevatorInfoService;

    @Autowired
    private IMaintenanceUnitService maintenanceUnitService;

    @Autowired
    private IMaintenanceTeamService maintenanceTeamService;

    @Autowired
    private IPropertyInfoService propertyInfoService;

    @Autowired
    private ICommunityService communityService;


    @PostMapping("/add")
    public BaseResponse create(@RequestBody ElevatorInfo elevator) {
        elevatorInfoService.save(elevator);
        return new BaseResponse(HttpStatus.OK.value(), "添加成功", elevator, null);
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        elevatorInfoService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "删除成功", null, null);
    }

    @PutMapping("/update")
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

//    @GetMapping("/export")
//    public void exportElevators(HttpServletResponse response) throws Exception {
//        // 设置响应头
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setCharacterEncoding("utf-8");
//        String fileName = URLEncoder.encode("电梯信息列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
//        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
//
//
//        List<ElevatorInfo> list = elevatorInfoService.list(); // 从数据库查所有
//
//        // 转换为 Excel DTO
//        List<ElevatorInfoTemplateExcel> dtoList = list.stream()
//                .map(ElevatorInfoExcelConverter::toDto)
//                .collect(Collectors.toList());
//
//        System.out.println("list size:" + dtoList.toString());
//
//        // 写入 Excel
//        ExcelUtil.exportExcelToTargetWithTemplate(response, null, "电梯信息", dtoList, ElevatorInfoTemplateExcel.class, "doc/elevator.xlsx");
//    }

    @PostMapping("/import")
    public BaseResponse importElevators(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 校验是否为 Excel 文件（可选）
            if (!isExcelFile(file)) {
                return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "请上传 .xls 或 .xlsx 文件", null, null);
            }

            // 2. 导入解析
            List<ElevatorImportTemplateExcel> dtoList = ExcelUtil.importExcel(file, ElevatorImportTemplateExcel.class);

            System.out.println("dtoList size:" + dtoList.size());
            System.out.println("dtoList:" + dtoList.toString());

            for (ElevatorImportTemplateExcel dto : dtoList) {
                //读取电梯信息，并写入
                ElevatorInfo elevatorInfo = ElevatorImportExcelConverter.toElevatorEntity(dto);
                if (elevatorInfo != null &&
                        StringUtils.isNotBlank(elevatorInfo.getElevatorNo()) &&
                        !"电梯编号".equals(elevatorInfo.getElevatorNo().trim())) {
                    Community communityEntity = ElevatorImportExcelConverter.toCommunityEntity(dto);
                    long communityId = communityService.getOrCreateCommunityId(communityEntity);

                    //读取使用小区信息，并写入
                    PropertyInfo propertyEntity = ElevatorImportExcelConverter.toPropertyEntity(dto);
                    long UsingUnitId = propertyInfoService.getOrCreatePropertyId(propertyEntity);

                    //读取维保信息，并写入
                    MaintenanceDTO maintenanceEntity = ElevatorImportExcelConverter.toMaintenanceEntity(dto);
                    long maintenanceUnitId = maintenanceUnitService.getOrCreateMaintenanceUnitId(maintenanceEntity.getMaintenanceUnit());

                    maintenanceEntity.getMaintenanceTeam().setMaintenanceUnitId(maintenanceUnitId);
                    long maintenanceTeamId = maintenanceTeamService.getOrCreateMaintenanceTeamId(maintenanceEntity.getMaintenanceTeam());

                    elevatorInfo.setMaintenanceUnitId(maintenanceUnitId);
                    elevatorInfo.setMaintenanceTeamId(maintenanceTeamId);
                    elevatorInfo.setUsingUnitId(UsingUnitId);

                    elevatorInfoService.createElevatorInfo(elevatorInfo);
                }
            }

            return new BaseResponse(HttpStatus.OK.value(), "成功导入 " + dtoList.size() + " 条电梯信息", null, null);
//            return new BaseResponse(HttpStatus.OK.value(), "成功导入 " + entities.size() + " 条电梯信息", entities, null);
        } catch (Exception e) {
            System.out.println("Excel 导入失败:" + e);
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "导入失败: " + e.getMessage(), null, null);
        }
    }
}