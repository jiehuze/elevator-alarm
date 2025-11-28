package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.NearbyMaintenanceDTO;
import com.schedule.elevator.entity.Maintenance;
import com.schedule.elevator.service.IMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}