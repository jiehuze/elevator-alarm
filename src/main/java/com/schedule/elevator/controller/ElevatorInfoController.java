package com.schedule.elevator.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.SearchInfoDTO;
import com.schedule.elevator.entity.ElevatorInfo;
import com.schedule.elevator.service.IElevatorInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @ModelAttribute SearchInfoDTO searchInfoDto) {
        Page<ElevatorInfo> page = new Page<>(current, size);
        IPage<ElevatorInfo> result = elevatorInfoService.pageElevators(page, searchInfoDto);
        return new BaseResponse(HttpStatus.OK.value(), "查询成功", result, null);
    }
}