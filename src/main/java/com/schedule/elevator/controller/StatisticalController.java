package com.schedule.elevator.controller;

import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.*;
import com.schedule.elevator.service.IElevatorInfoService;
import com.schedule.elevator.service.IFaultRecordService;
import com.schedule.elevator.service.IWorkOrderProgressService;
import com.schedule.elevator.service.IWorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistical")
public class StatisticalController {

    @Autowired
    private IWorkOrderService workOrderService;

    @Autowired
    private IWorkOrderProgressService workOrderProgressService;

    @Autowired
    private IElevatorInfoService elevatorInfoService;

    @Autowired
    private IFaultRecordService faultRecordService;

    /*******************************************电梯统计******************************************/
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

    @GetMapping("/statistical")
    public BaseResponse statistical(@ModelAttribute SearchDTO workOrderDTO) {
        List<FaultResultDTO> faultResultDTOS = faultRecordService.statisticalFault(workOrderDTO.getCreateTimeStart(), workOrderDTO.getCreateTimeEnd());

        return new BaseResponse(HttpStatus.OK.value(), "查询成功", faultResultDTOS, null);
    }

    /*******************************************电梯故障统计******************************************/

    /**
     * 月份	处置事件总数（起）	困人（起）	非困人（起）	其他（起）	解救被困人数（人）	困人救援到达现场平均（分钟）	非困人救援到达现场平均（分钟）	实施救援平均用时（分钟）
     * 12月份	127	57	56	4	112	8.39	9.05	2.0
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/workorder-type-count")
    public BaseResponse countWorkOrder(@ModelAttribute SearchDTO searchDTO) {
        WorkOrderStatisticsDTO result = workOrderService.getWorkOrderStatisticsByCondition(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", result, null);
    }

    /**
     * 按时间段统计故障数量: 0-1, 1-2, 2-3, 3-4, 4-5, 5-6, 6-7, 7-8, 8-9, 9-10, 10-11, 11-12, 12-13, 13-14, 14-15, 15-16, 16-17, 17-18, 18-19, 19-20, 20-21, 21-22, 22-23, 23-24
     */
    @GetMapping("/fault-timeline-slot")
    public BaseResponse getFaultTimeline(SearchDTO searchDTO) {
        List<TimeSlotStatsDTO> stats = workOrderService.getFaultStatsByTimeSlot(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", stats, null);
    }

    /**
     * 根据工单中的，1，,2，,3级统计数量，并统计总数量
     */
    @GetMapping("/rescue-level-count")
    public BaseResponse countRescueLevel(@ModelAttribute SearchDTO searchDTO) {
        RescueLevelStatsDTO rescueLevelStats = workOrderService.getRescueLevelStats(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", rescueLevelStats, null);
    }
}
