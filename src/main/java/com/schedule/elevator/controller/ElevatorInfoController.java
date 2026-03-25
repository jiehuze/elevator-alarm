package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.BrandElevatorStatisticsDTO;
import com.schedule.elevator.dto.ElevatorInfoDTO;
import com.schedule.elevator.dto.MaintenanceDTO;
import com.schedule.elevator.dto.SearchDTO;
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

    @Autowired
    private ISafetyOfficerService safetyOfficerService;


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

    @DeleteMapping("/batchDelete")
    public BaseResponse batchDelete(@RequestBody List<Long> ids) {
        elevatorInfoService.removeByIds(ids);
        return new BaseResponse(HttpStatus.OK.value(), "批量删除成功", true, null);
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
            if (elevator.getMaintenanceUnitExpired()) {
                elevator.setMaintenanceUnit("");
                elevator.setMaintenanceUnitId(0l);
                elevator.setMaintenanceType("无");
                elevator.setMaintenanceTeamId(0l);
                elevator.setMaintenancePersonnelName("");
                elevator.setMaintenancePersonnelId(0l);
            }

            if (elevator.getUsingUnitExpired()) {
                elevator.setUsingUnit("");
                elevator.setUsingUnitId(0l);
                elevator.setSafetyOfficerId(0l);
            }
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
            @ModelAttribute SearchDTO elevatorInfoDTO) {
        Page<ElevatorInfo> page = new Page<>(current, size);
        IPage<ElevatorInfo> result = elevatorInfoService.pageElevators(page, elevatorInfoDTO);
        for (ElevatorInfo info : result.getRecords()) {
            if (info.getMaintenancePersonnelId() != null) {
                MaintenancePersonnel maintenancePersonnel = maintenancePersonnelService.getById(info.getMaintenancePersonnelId());
                if (maintenancePersonnel != null) {
                    info.setMaintenancePersonnelPhone(maintenancePersonnel.getPhone());
                }
            }
        }
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", result, null);
    }

    @GetMapping("/search")
    public BaseResponse search(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute SearchDTO elevatorInfoDTO) {
        Page<ElevatorInfo> page = new Page<>(current, size);
        IPage<ElevatorInfo> result = elevatorInfoService.pageElevators(page, elevatorInfoDTO);
        for (ElevatorInfo info : result.getRecords()) {
            MaintenancePersonnel maintenancePersonnel = maintenancePersonnelService.getById(info.getMaintenancePersonnelId());
            info.setMaintenancePersonnelPhone(maintenancePersonnel.getPhone());
        }
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", result, null);
    }

    @GetMapping("/export")
    public void exportElevators(@ModelAttribute SearchDTO elevatorInfoDTO,
                                HttpServletResponse response) throws Exception {
        String fileName = URLEncoder.encode("电梯信息列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        List<ElevatorInfo> list = elevatorInfoService.listElevators(elevatorInfoDTO); // 从数据库查所有
        List<ElevatorImportTemplateExcel> dtoList = new ArrayList<>();
        for (ElevatorInfo info : list) {
            Community community = communityService.getById(info.getCommunityId());
            SafetyOfficer safetyOfficer = safetyOfficerService.getById(info.getSafetyOfficerId());
            PropertyInfo propertyInfo = propertyInfoService.getById(info.getUsingUnitId());
            MaintenanceUnit maintenanceUnit = maintenanceUnitService.getById(info.getMaintenanceUnitId());
            MaintenanceTeam maintenanceTeam = maintenanceTeamService.getById(info.getMaintenanceTeamId());
            MaintenancePersonnel maintenancePersonnel = maintenancePersonnelService.getById(info.getMaintenancePersonnelId());

            ElevatorImportTemplateExcel dto = ElevatorImportExcelConverter.toDTO(info, community, safetyOfficer, propertyInfo, maintenanceUnit, maintenanceTeam, maintenancePersonnel);

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
//            System.out.println("dtoList:" + dtoList.toString());

            for (ElevatorImportTemplateExcel dto : dtoList) {
                System.out.println("execl: " + dto);
                //读取电梯信息，并写入
                ElevatorInfo elevatorInfo = ElevatorImportExcelConverter.toElevatorEntity(dto);
                if (elevatorInfo == null) {
                    return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "电梯使用状态错误", dto, null);
                }
                System.out.println("elevatorInfo: " + elevatorInfo.toString());


                if (StringUtils.isBlank(elevatorInfo.getElevatorNo()) ||
                        StringUtils.isBlank(elevatorInfo.getRescueCode())) {
                    System.out.println("elevatorInfo  no: " + elevatorInfo.getElevatorNo());
                    System.out.println("elevatorInfo  rescueCode: " + elevatorInfo.getRescueCode());
                    return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "电梯编码或者救援码不能为空，请完善", dto, null);
                }
                ElevatorInfo ele = elevatorInfoService.searchElevatorInfo(new SearchDTO().setRegisterCode(elevatorInfo.getRegisterCode()));
                if (ele != null) {
                    if (!ele.getRescueCode().equals(elevatorInfo.getRescueCode())) {
                        return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "该电梯已存在,救援码不一致", dto, null);
                    }
                }


                //读取使用小区信息，并写入
                PropertyInfo propertyEntity = ElevatorImportExcelConverter.toPropertyEntity(dto);
                long UsingUnitId = propertyInfoService.getOrCreatePropertyId(propertyEntity);

                SafetyOfficer safetyOfficerEntity = ElevatorImportExcelConverter.toSafetyOfficerEntity(dto);
                safetyOfficerEntity.setUsingUnitId(UsingUnitId);
                safetyOfficerEntity.setUsingUnit(propertyEntity.getUsingUnit());
                long safetyOfficerId = safetyOfficerService.getOrCreateSafetyOfficerId(safetyOfficerEntity);

                Community communityEntity = ElevatorImportExcelConverter.toCommunityEntity(dto);
                communityEntity.setUsingUnitId(UsingUnitId);
                communityEntity.setSafetyOfficerId(safetyOfficerId);
                communityEntity.setSafetyOfficerName(safetyOfficerEntity.getSafetyOfficerName());
                long communityId = communityService.getOrCreateCommunityId(communityEntity);

                if (dto.getMaintenanceUnit() != null) {
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
                }
                elevatorInfo.setCommunityId(communityId);
                elevatorInfo.setUsingUnitId(UsingUnitId);
                elevatorInfo.setSafetyOfficerId(safetyOfficerId);

                try {
                    elevatorInfoService.createElevatorInfo(elevatorInfo);
                } catch (Exception e) {
                    System.out.println("创建电梯信息失败:" + e.getMessage());
                    return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "创建电梯信息失败：" + e.getMessage(), dto, e.getMessage());
                }
            }

            return new BaseResponse(HttpStatus.OK.value(), "成功导入 " + dtoList.size() + " 条电梯信息", null, null);
        } catch (Exception e) {
            System.out.println("Excel 导入失败:" + e);
            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "导入失败: " + e.getMessage(), null, null);
        }
    }
}