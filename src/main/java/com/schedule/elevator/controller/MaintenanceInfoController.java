package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.entity.MaintenanceInfo;
import com.schedule.elevator.service.IMaintenanceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceInfoController {

    @Autowired
    private IMaintenanceInfoService maintenanceInfoService;

    @PostMapping("/add")
    public BaseResponse add(@RequestBody MaintenanceInfo maintenanceInfo) {
        maintenanceInfoService.save(maintenanceInfo);
        return new BaseResponse(HttpStatus.OK.value(), "维保信息添加成功", maintenanceInfo, null);
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        maintenanceInfoService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "维保信息删除成功", null, null);
    }

    @PutMapping("/update")
    public BaseResponse update(@RequestBody MaintenanceInfo maintenanceInfo) {
        maintenanceInfoService.updateById(maintenanceInfo);
        return new BaseResponse(HttpStatus.OK.value(), "维保信息更新成功", maintenanceInfo, null);
    }

    @GetMapping("/{id}")
    public BaseResponse get(@PathVariable Long id) {
        MaintenanceInfo info = maintenanceInfoService.getById(id);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", info, null);
    }

    @GetMapping("/list")
    public BaseResponse list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        Page<MaintenanceInfo> page = new Page<>(current, size);
        IPage<MaintenanceInfo> result = maintenanceInfoService.page(page);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", result, null);
    }
}