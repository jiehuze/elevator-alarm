package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.*;
import com.schedule.elevator.entity.MaintenancePersonnel;
import com.schedule.elevator.entity.MaintenanceTeam;
import com.schedule.elevator.entity.MaintenanceUnit;
import com.schedule.elevator.service.IElevatorInfoService;
import com.schedule.elevator.service.IMaintenancePersonnelService;
import com.schedule.elevator.service.IMaintenanceTeamService;
import com.schedule.elevator.service.IMaintenanceUnitService;
import com.schedule.elevator.service.impl.MaintenanceUnitServiceImpl;
import com.schedule.excel.MaintenanceExcelConverter;
import com.schedule.excel.MaintenancePersonnelExcel;
import com.schedule.excel.MaintenanceUnitExcel;
import com.schedule.utils.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 维保信息接口
 */
@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    @Autowired
    private IMaintenanceUnitService maintenanceUnitService;

    @Autowired
    private IMaintenanceTeamService maintenanceTeamService;

    @Autowired
    private IMaintenancePersonnelService maintenancePersonnelService;

    @Autowired
    private IElevatorInfoService elevatorInfoService;
    @Autowired
    private MaintenanceUnitServiceImpl maintenanceUnitServiceImpl;

    @PostMapping("/add")
    public BaseResponse add(@RequestBody MaintenanceUnit maintenance) {
        maintenanceUnitService.save(maintenance);
        return new BaseResponse(HttpStatus.OK.value(), "维保信息添加成功", maintenance, null);
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        maintenanceUnitService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "维保信息删除成功", null, null);
    }

    @PutMapping("/update")
    public BaseResponse update(@RequestBody MaintenanceUnit maintenance) {
        maintenanceUnitService.updateById(maintenance);
        return new BaseResponse(HttpStatus.OK.value(), "维保信息更新成功", maintenance, null);
    }

    @GetMapping("/{id}")
    public BaseResponse get(@PathVariable Long id) {
        MaintenanceUnit info = maintenanceUnitService.getById(id);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", info, null);
    }

    @GetMapping("/list")
    public BaseResponse list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute MaintenanceUnit searchInfo) {
        IPage<MaintenanceUnit> result = maintenanceUnitService.page(searchInfo, current, size);

        if (result != null) {
            for (MaintenanceUnit info : result.getRecords()) {
                ElevatorInfoDTO elevatorInfoDTO = new ElevatorInfoDTO();
                elevatorInfoDTO.setMaintenanceUnitId(info.getId());

                info.setCount(elevatorInfoService.count(elevatorInfoDTO));
            }
        }

        return new BaseResponse(HttpStatus.OK.value(), "查询成功", result, null);
    }

    @GetMapping("/unit/{id}")
    public BaseResponse getMaintenanceUnitById(@PathVariable Long id) {
        MaintenanceUnit unit = maintenanceUnitService.getById(id);
        List<MaintenanceTeam> teams = maintenanceTeamService.getByTeamAndUnitId(null, id);

        for (MaintenanceTeam team : teams) {

            List<MaintenancePersonnel> list = maintenancePersonnelService.listByTeamId(team.getId());
            team.setPersons(list);

            team.setNumbers((long) list.size());
        }
        unit.setTeams(teams);

        return new BaseResponse(HttpStatus.OK.value(), "查询成功", unit, null);
    }

    @GetMapping("/level")
    public BaseResponse getMaintenanceUnitByLevel(@ModelAttribute SearchDTO maintenanceQueryDTO) {

        List<MaintenanceUnit> units = maintenanceUnitService.listByQuery(maintenanceQueryDTO);

        for (MaintenanceUnit unit : units) {
//            List<MaintenanceTeam> teams = maintenanceTeamService.getByTeamAndUnitId(null, unit.getId());
            MaintenanceTeam maintenanceTeam = new MaintenanceTeam();
            maintenanceTeam.setMaintenanceUnitId(unit.getId());
            maintenanceTeam.setDistrict(maintenanceQueryDTO.getDistrict());
            List<MaintenanceTeam> teams = maintenanceTeamService.listByDt(maintenanceTeam);

            for (MaintenanceTeam team : teams) {

                List<MaintenancePersonnel> list = maintenancePersonnelService.listByTeamId(team.getId());
                team.setPersons(list);

                team.setNumbers((long) list.size());
            }
            unit.setTeams(teams);
        }

        return new BaseResponse(HttpStatus.OK.value(), "查询成功", units, null);
    }

    @GetMapping("/team/nearby")
    public BaseResponse getNearby(@ModelAttribute NearbyMaintenanceDTO nearbyMaintenanceDTO) {
        List<NearbyMaintenanceDTO> nearby = maintenanceUnitService.getNearby(
                nearbyMaintenanceDTO.getLatitude(),
                nearbyMaintenanceDTO.getLongitude(),
                nearbyMaintenanceDTO.getDistanceKm());
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", nearby, null);
    }

    @GetMapping("/info")
    public BaseResponse getMaintenanceInfo(@ModelAttribute MaintenanceQueryDTO maintenanceQueryDTO) {
        HashMap<String, Object> info = new HashMap<>();

        // 人员信息
        MaintenancePersonnel person = maintenancePersonnelService.getById(maintenanceQueryDTO.getMaintenancePersonnelId());

        // 团队信息
        MaintenanceTeam team = maintenanceTeamService.getById(person.getMaintenanceTeamId());
        if (team != null) {
            maintenanceQueryDTO.setMaintenanceTeamId(team.getId());
        } else {
            team = maintenanceTeamService.getById(maintenanceQueryDTO.getMaintenanceTeamId());
        }
        // 单位信息
        MaintenanceUnit unit = maintenanceUnitService.getById(team.getMaintenanceUnitId());
        // 返回
        info.put("unit", unit);
        info.put("team", team);
        info.put("person", person);

        return new BaseResponse(HttpStatus.OK.value(), "查询成功", info, null);
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


    @GetMapping("/export-person")
    public void exportPerson(@ModelAttribute SearchDTO searchDTO,
                             HttpServletResponse response) throws Exception {

        String fileName = URLEncoder.encode("维修人员信息", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        List<MaintenancePersonnel> list = maintenancePersonnelService.listBySearchDTO(searchDTO);
        List<MaintenancePersonnelExcel> dtoList = new ArrayList<>();
        for (MaintenancePersonnel personnel : list) {
            MaintenancePersonnelExcel dto = MaintenanceExcelConverter.toPersonDto(personnel);
            dtoList.add(dto);
        }

        System.out.println("list size:" + dtoList.toString());

        // 写入 Excel
        ExcelUtil.exportExcelToTargetWithTemplate(response, fileName, "维保信息", dtoList, MaintenancePersonnelExcel.class, "doc/maintenance_person.xlsx");
    }

    @GetMapping("/export-unit")
    public void exportUnit(@ModelAttribute SearchDTO queryDTO,
                           HttpServletResponse response) throws Exception {
        // 设置响应头
        String fileName = URLEncoder.encode("维保单位信息", StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        List<MaintenanceUnit> dtoList = maintenanceUnitService.listByQuery(queryDTO);
        System.out.println("list size:" + dtoList.toString());

        // 写入 Excel
        ExcelUtil.exportExcelToTargetWithTemplate(response, fileName, "维保信息", dtoList, MaintenanceUnitExcel.class, "doc/maintenance_unit.xlsx");
    }

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