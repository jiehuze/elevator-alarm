package com.schedule.elevator.controller;

import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.SearchDTO;
import com.schedule.elevator.service.IElevatorInfoService;
import com.schedule.elevator.service.IWorkOrderProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistical")
public class StatisticalController {

    @Autowired
    private IWorkOrderProgressService workOrderProgressService;

    @Autowired
    private IElevatorInfoService elevatorInfoService;

    /**
     * 按电梯类型统计数量（支持时间范围筛选）
     */
    @GetMapping("/elevator-type-count")
    public BaseResponse countElevatorType(@ModelAttribute SearchDTO searchDTO) {
        List<Map<String, Object>> elTypeCount = elevatorInfoService.countByElevatorType(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", elTypeCount, null);
    }

    /**
     * 按区县统计电梯数量及占比
     */
    @GetMapping("/elevator-district-count")
    public BaseResponse countByDistrict(@ModelAttribute SearchDTO searchDTO) {
        List<Map<String, Object>> elTypeCount = elevatorInfoService.countByDistrict(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", elTypeCount, null);
    }

    /**
     * 统计时间段内新增电梯数
     */
    @GetMapping("/new-elevators")
    public BaseResponse countNewElevators(@ModelAttribute SearchDTO searchDTO) {
        Map<String, Object> result = elevatorInfoService.countNewElevators(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", result, null);
    }
}
