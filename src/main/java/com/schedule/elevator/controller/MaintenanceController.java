package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.ElevatorInfoDTO;
import com.schedule.elevator.dto.NearbyMaintenanceUnitDTO;
import com.schedule.elevator.dto.TeamPersonDTO;
import com.schedule.elevator.entity.MaintenancePersonnel;
import com.schedule.elevator.entity.MaintenanceTeam;
import com.schedule.elevator.entity.MaintenanceUnit;
import com.schedule.elevator.service.IElevatorInfoService;
import com.schedule.elevator.service.IMaintenancePersonnelService;
import com.schedule.elevator.service.IMaintenanceTeamService;
import com.schedule.elevator.service.IMaintenanceUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementPermission;
import java.util.List;

/**
 * 维保信息接口
 */
@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    @Autowired
    private IMaintenanceUnitService maintenanceInfoService;

    @Autowired
    private IMaintenanceTeamService maintenanceTeamService;

    @Autowired
    private IMaintenancePersonnelService maintenancePersonnelService;

    @Autowired
    private IElevatorInfoService elevatorInfoService;

    @PostMapping("/add")
    public BaseResponse add(@RequestBody MaintenanceUnit maintenance) {
        maintenanceInfoService.save(maintenance);
        return new BaseResponse(HttpStatus.OK.value(), "维保信息添加成功", maintenance, null);
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        maintenanceInfoService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "维保信息删除成功", null, null);
    }

    @PutMapping("/update")
    public BaseResponse update(@RequestBody MaintenanceUnit maintenance) {
        maintenanceInfoService.updateById(maintenance);
        return new BaseResponse(HttpStatus.OK.value(), "维保信息更新成功", maintenance, null);
    }

    @GetMapping("/{id}")
    public BaseResponse get(@PathVariable Long id) {
        MaintenanceUnit info = maintenanceInfoService.getById(id);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", info, null);
    }

    @GetMapping("/list")
    public BaseResponse list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute MaintenanceUnit searchInfo) {
        IPage<MaintenanceUnit> result = maintenanceInfoService.page(searchInfo, current, size);

        if (result != null) {
            for (MaintenanceUnit info : result.getRecords()) {
                ElevatorInfoDTO elevatorInfoDTO = new ElevatorInfoDTO();
                elevatorInfoDTO.setMaintenanceUnitId(info.getId());

                info.setCount(elevatorInfoService.count(elevatorInfoDTO));
            }
        }

        return new BaseResponse(HttpStatus.OK.value(), "查询成功", result, null);
    }

    @GetMapping("/nearby")
    public BaseResponse getNearby(@ModelAttribute NearbyMaintenanceUnitDTO nearbyMaintenanceDTO) {
        List<NearbyMaintenanceUnitDTO> nearby = maintenanceInfoService.getNearby(
                nearbyMaintenanceDTO.getLatitude(),
                nearbyMaintenanceDTO.getLongitude(),
                nearbyMaintenanceDTO.getDistanceKm());
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", nearby, null);
    }

    /****************************** 分组信息 *********************************/
    @GetMapping("/teams")
    public BaseResponse getMaintenanceTeams(@RequestParam(defaultValue = "1") int current,
                                            @RequestParam(defaultValue = "10") int size,
                                            @ModelAttribute MaintenanceTeam searchTeam) {
        IPage<MaintenanceTeam> maintenanceTeams = maintenanceTeamService.page(searchTeam, current, size);
        for (MaintenanceTeam team : maintenanceTeams.getRecords()) {
            MaintenancePersonnel maintenancePersonnel = new MaintenancePersonnel();
            maintenancePersonnel.setMaintenanceTeamId(team.getId());

            team.setNumbers(maintenancePersonnelService.count(maintenancePersonnel));
        }
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", maintenanceTeams, null);
    }

    @PostMapping("/team/update")
    public BaseResponse updateMaintenanceTeam(@RequestBody MaintenanceTeam team) {
        maintenanceTeamService.updateById(team);
        return new BaseResponse(HttpStatus.OK.value(), "维保团队更新成功", team, null);
    }

    @PostMapping("/team/add")
    public BaseResponse create(@RequestBody MaintenanceTeam team) {
        maintenanceTeamService.save(team);
        return new BaseResponse(HttpStatus.OK.value(), "维保团队添加成功", team, null);
    }

    @PostMapping("/team/person")
    public BaseResponse addTeamPerson(@RequestBody TeamPersonDTO teamPersonDTO) {
        if (teamPersonDTO.getMaintenanceTeamId() == null) {
            return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "参数错误", null, null);
        }

        if (teamPersonDTO.getAddMaintenancePersonnelIds() != null) {
            for (Long id : teamPersonDTO.getAddMaintenancePersonnelIds()) {
                MaintenancePersonnel personnel = maintenancePersonnelService.getById(id);
                personnel.setMaintenanceTeamId(teamPersonDTO.getMaintenanceTeamId());
                maintenancePersonnelService.updateById(personnel);
            }
        }
        if (teamPersonDTO.getDeleteMaintenancePersonnelIds() != null) {
            for (Long id : teamPersonDTO.getDeleteMaintenancePersonnelIds()) {
                MaintenancePersonnel personnel = maintenancePersonnelService.getById(id);
                personnel.setMaintenanceTeamId(null);
                maintenancePersonnelService.updateById(personnel);
            }
        }

        return new BaseResponse(HttpStatus.OK.value(), "添加维保团队人员成功", null, null);
    }

    /****************************** 人员信息 *********************************/
    @GetMapping("/persons")
    public BaseResponse getMaintenancePersons(@RequestParam(defaultValue = "1") int current,
                                              @RequestParam(defaultValue = "10") int size,
                                              @ModelAttribute MaintenancePersonnel searchPerson) {
        IPage<MaintenancePersonnel> maintenanceTeamPage = maintenancePersonnelService.pagePersonnels(searchPerson, current, size);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", maintenanceTeamPage, null);
    }

    /**
     * 添加维保人员
     */
    @PostMapping("/person/add")
    public BaseResponse create(@RequestBody MaintenancePersonnel personnel) {
        maintenancePersonnelService.save(personnel);
        return new BaseResponse(HttpStatus.OK.value(), "添加成功", personnel, null);
    }

    /**
     * 更新维保人员
     */
    @PutMapping("/person/update")
    public BaseResponse update(@RequestBody MaintenancePersonnel personnel) {
        maintenancePersonnelService.updateById(personnel);
        return new BaseResponse(HttpStatus.OK.value(), "更新成功", personnel, null);
    }


//    @GetMapping("/export")
//    public void exportElevators(HttpServletResponse response) throws Exception {
//        // 设置响应头
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setCharacterEncoding("utf-8");
//        String fileName = URLEncoder.encode("维保信息列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
//        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
//
//
//        List<MaintenanceUnit> list = maintenanceInfoService.list(); // 从数据库查所有
//
//        // 转换为 Excel DTO
//        List<MaintenanceTemplateExcel> dtoList = list.stream()
//                .map(MaintenanceExcelConverter::toDto)
//                .collect(Collectors.toList());
//
//        System.out.println("list size:" + dtoList.toString());
//
//        // 写入 Excel
//        ExcelUtil.exportExcelToTargetWithTemplate(response, null, "维保信息", dtoList, MaintenanceTemplateExcel.class, "doc/maintenance.xlsx");
//    }

//    @PostMapping("/import")
//    public BaseResponse importElevators(@RequestParam("file") MultipartFile file) {
//        try {
//            // 1. 校验是否为 Excel 文件（可选）
//            if (!isExcelFile(file)) {
//                return new BaseResponse(HttpStatus.BAD_REQUEST.value(), "请上传 .xls 或 .xlsx 文件", null, null);
//            }
//
//            // 2. 导入解析
//            List<MaintenanceTemplateExcel> dtoList = ExcelUtil.importExcel(file, MaintenanceTemplateExcel.class);
//
//            System.out.println("dtoList size:" + dtoList.size());
//            // 3. 转换为实体并保存（注意空值处理）
//            List<MaintenanceUnit> entities = dtoList.stream()
//                    .map(MaintenanceExcelConverter::toEntity) // 使用你已有的静态方法
//                    .filter(e -> e != null &&
////                            StringUtils.isNotBlank(e.getElevatorNo()) &&
//                            !"电梯编号".equals(e.getMaintainerName().trim()))
//                    .collect(Collectors.toList());
//
//            // 4. 批量保存（根据业务决定是否去重、校验等）
//            maintenanceInfoService.saveBatch(entities);
//
//            return new BaseResponse(HttpStatus.OK.value(), "成功导入 " + entities.size() + " 条维保信息", null, null);
//        } catch (Exception e) {
//            System.out.println("Excel 导入失败:" + e);
//            return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "导入失败: " + e.getMessage(), null, null);
//        }
//    }
}