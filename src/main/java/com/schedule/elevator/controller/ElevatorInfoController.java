package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.ElevatorInfoDTO;
import com.schedule.elevator.dto.MaintenanceDTO;
import com.schedule.elevator.entity.*;
import com.schedule.elevator.service.*;
import com.schedule.excel.ElevatorImportExcelConverter;
import com.schedule.excel.ElevatorImportTemplateExcel;
import com.schedule.utils.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    private IMaintenancePersonnelService maintenancePersonnelService;

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
        elevatorInfoService.update(elevator,
                new LambdaQueryWrapper<ElevatorInfo>().eq(ElevatorInfo::getId, elevator.getId()));
        return new BaseResponse(HttpStatus.OK.value(), "更新成功", elevator, null);
    }

    @PutMapping("/batchUpdate")
    public BaseResponse batchUpdate(@RequestBody List<ElevatorInfo> elevatorList) {
        for (ElevatorInfo elevator : elevatorList) {
            elevatorInfoService.update(elevator,
                    new LambdaQueryWrapper<ElevatorInfo>().eq(ElevatorInfo::getId, elevator.getId()));
        }
        return new BaseResponse(HttpStatus.OK.value(), "更新成功", true, null);
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

    @GetMapping("/search")
    public BaseResponse search(@ModelAttribute ElevatorInfoDTO elevatorInfoDTO) {
        List<ElevatorInfo> elevatorInfos = elevatorInfoService.listElevators(elevatorInfoDTO);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", elevatorInfos, null);
    }

    @GetMapping("/export")
    public void exportElevators(@ModelAttribute ElevatorInfoDTO elevatorInfoDTO,
                                HttpServletResponse response) throws Exception {
        String fileName = URLEncoder.encode("电梯信息列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        List<ElevatorInfo> list = elevatorInfoService.listElevators(elevatorInfoDTO); // 从数据库查所有
        List<ElevatorImportTemplateExcel> dtoList = new ArrayList<>();
        for (ElevatorInfo info : list) {
            Community community = communityService.getById(info.getCommunityId());
            PropertyInfo propertyInfo = propertyInfoService.getById(info.getUsingUnitId());
            MaintenanceUnit maintenanceUnit = maintenanceUnitService.getById(info.getMaintenanceUnitId());
            MaintenanceTeam maintenanceTeam = maintenanceTeamService.getById(info.getMaintenanceTeamId());
            MaintenancePersonnel maintenancePersonnel = maintenancePersonnelService.getById(info.getMaintenancePersonnelId());

            ElevatorImportTemplateExcel dto = ElevatorImportExcelConverter.toDTO(info, community, propertyInfo, maintenanceUnit, maintenanceTeam, maintenancePersonnel);

            dtoList.add(dto);
        }

        System.out.println("list size:" + dtoList.toString());

        // 写入 Excel
        ExcelUtil.exportExcelToTargetWithTemplate(response, fileName, "电梯信息", dtoList, ElevatorImportTemplateExcel.class, "doc/elevator.xlsx");
    }

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

                    //读取使用小区信息，并写入
                    PropertyInfo propertyEntity = ElevatorImportExcelConverter.toPropertyEntity(dto);
                    long UsingUnitId = propertyInfoService.getOrCreatePropertyId(propertyEntity);

                    Community communityEntity = ElevatorImportExcelConverter.toCommunityEntity(dto);
                    communityEntity.setUsingUnitId(UsingUnitId);
                    long communityId = communityService.getOrCreateCommunityId(communityEntity);

                    //读取维保信息，并写入
                    MaintenanceDTO maintenanceEntity = ElevatorImportExcelConverter.toMaintenanceEntity(dto);
                    long maintenanceUnitId = maintenanceUnitService.getOrCreateMaintenanceUnitId(maintenanceEntity.getMaintenanceUnit());

                    //读取维保团队信息，并写入
                    maintenanceEntity.getMaintenanceTeam().setMaintenanceUnitId(maintenanceUnitId);
                    long maintenanceTeamId = maintenanceTeamService.getOrCreateMaintenanceTeamId(maintenanceEntity.getMaintenanceTeam());

                    //读取维保人员信息，并写入
                    maintenanceEntity.getMaintenancePersonnel().setMaintenanceUnitId(maintenanceUnitId);
                    maintenanceEntity.getMaintenancePersonnel().setMaintenanceTeamId(maintenanceTeamId);
                    long maintenancePersonnelId = maintenancePersonnelService.getOrCreatePersonnelId(maintenanceEntity.getMaintenancePersonnel());

                    elevatorInfo.setMaintenanceUnitId(maintenanceUnitId);
                    elevatorInfo.setMaintenanceTeamId(maintenanceTeamId);
                    elevatorInfo.setMaintenancePersonnelId(maintenancePersonnelId);
                    elevatorInfo.setCommunityId(communityId);
                    elevatorInfo.setUsingUnitId(UsingUnitId);

                    elevatorInfoService.createElevatorInfo(elevatorInfo);
//                    elevatorInfoService.saveOrUpdate(elevatorInfo, new LambdaQueryWrapper<ElevatorInfo>().eq(ElevatorInfo::getRescueCode, elevatorInfo.getRescueCode()));
                }
            }

            return new BaseResponse(HttpStatus.OK.value(), "成功导入 " + dtoList.size() + " 条电梯信息", null, null);
        } catch (Exception e) {
            System.out.println("Excel 导入失败:" + e);
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "导入失败: " + e.getMessage(), null, null);
        }
    }
}