package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.ElevatorInfoDTO;
import com.schedule.elevator.dto.NearbyMaintenanceUnitDTO;
import com.schedule.elevator.entity.MaintenanceTeam;
import com.schedule.elevator.entity.MaintenanceUnit;
import com.schedule.elevator.service.IElevatorInfoService;
import com.schedule.elevator.service.IMaintenanceTeamService;
import com.schedule.elevator.service.IMaintenanceUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/teams")
    public BaseResponse getMaintenanceTeams(@RequestParam(defaultValue = "1") int current,
                                            @RequestParam(defaultValue = "10") int size,
                                            @ModelAttribute MaintenanceTeam searchTeam) {
        System.out.println("---------------searchTeam:" + searchTeam);

        IPage<MaintenanceTeam> maintenanceTeams = maintenanceTeamService.page(searchTeam, current, size);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", maintenanceTeams, null);
    }

    @PostMapping("/team/update")
    public BaseResponse updateMaintenanceTeam(@RequestBody MaintenanceTeam team) {
        System.out.println("---------------team:" + team);
        maintenanceTeamService.updateById(team);
        return new BaseResponse(HttpStatus.OK.value(), "维保团队更新成功", team, null);
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