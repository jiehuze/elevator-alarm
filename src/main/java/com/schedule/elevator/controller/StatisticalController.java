package com.schedule.elevator.controller;

import com.schedule.common.BaseResponse;
import com.schedule.elevator.dto.*;
import com.schedule.elevator.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private ISysDistrictService sysDistrictService;

    /*******************************************电梯统计******************************************/
    /**
     * 按电梯类型统计数量（支持时间范围筛选）
     */
    @GetMapping("/elevator-type-count")
    public BaseResponse countElevatorType(@ModelAttribute SearchDTO searchDTO) {
        List<ElevatorTypeStatsDTO> dtoList = elevatorInfoService.statsByElevatorType(searchDTO);

        return new BaseResponse(HttpStatus.OK.value(), "success", dtoList, null);
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

    @GetMapping("/fault-list-statistical")
    public BaseResponse statistical(@ModelAttribute SearchDTO workOrderDTO) {
        List<FaultResultDTO> faultResultDTOS = faultRecordService.statisticalFault(workOrderDTO);

        return new BaseResponse(HttpStatus.OK.value(), "查询成功", faultResultDTOS, null);
    }

    /*******************************************电梯故障统计******************************************/

    @GetMapping("/duplicate-orders")
    public BaseResponse getOrdersByDuplicateRescueCode(@ModelAttribute SearchDTO searchDTO) {
//        HashMap<String, DuplicateOrderDTO> oMap = workOrderService.getOrdersByDuplicateRescueCode(searchDTO);
        List<SecondaryFaultStatsDTO> list = workOrderService.getOrdersByDuplicateRescueCode(searchDTO);

        return new BaseResponse(HttpStatus.OK.value(), "success", list, null);
    }

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

    /**
     * 按项目类型统计数量,
     * RESIDENTIAL_AREA("住宅小区", "RESIDENTIAL"),
     * OFFICE_AREA("办公区域", "OFFICE"),
     * MALL_SUPERMARKET("商场超市", "MALL"),
     * HOTEL_RESTAURANT("宾馆饭店", "HOTEL"),
     * HOSPITAL("医院", "HOSPITAL"),
     * SCHOOL("学校", "SCHOOL"),
     * TRANSPORTATION("交通场所", "TRANSPORTATION"),
     * CULTURAL_ENTERTAINMENT("文体娱乐馆", "CULTURAL"),
     * OTHER_PLACE("其他场所", "OTHER");
     */
    @GetMapping("/project-type-count")
    public BaseResponse countProjectType(@ModelAttribute SearchDTO searchDTO) {
        ProjectTypeStatItemDTO projectTypeStats = workOrderService.getProjectTypeStats(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", projectTypeStats, null);
    }

    @GetMapping("/time-consumption-stats")
    public BaseResponse getTimeConsumptionStats(@ModelAttribute SearchDTO searchDTO) {
        List<TimeConsumptionStatsDTO> timeConsumptionStats = workOrderService.getTimeConsumptionStats(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", timeConsumptionStats, null);
    }

    @GetMapping("/overtime-list")
    public BaseResponse getOvertimeWorkOrders(@ModelAttribute SearchDTO searchDTO) {
        List<OvertimeWorkOrderDTO> overtimeWorkOrders = workOrderService.getOvertimeWorkOrders(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", overtimeWorkOrders, null);
    }

    @GetMapping("/district-stats")
    public BaseResponse getDistrictStats(@ModelAttribute SearchDTO searchDTO) {
        List<DistrictStatisticsDTO> stats = workOrderService.getDistrictStatistics(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", stats, null);
    }

    @GetMapping("/maintenance-unit-fault-rate")
    public BaseResponse getMaintenanceUnitFaultRate(@ModelAttribute SearchDTO searchDTO) {
        List<MaintenanceUnitFaultRateDTO> faultRate = workOrderService.getMaintenanceUnitFaultRate(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", faultRate, null);
    }

    @GetMapping("/using-unit-fault-rate")
    public BaseResponse getUsingUnitFaultRate(@ModelAttribute SearchDTO searchDTO) {
        List<UsingUnitFaultRateDTO> faultRate = workOrderService.getUsingUnitFaultRate(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", faultRate, null);
    }

    @GetMapping("/brand-fault-rate")
    public BaseResponse getBrandFaultRate(@ModelAttribute SearchDTO searchDTO) {
        List<ElevatorBrandFaultRateDTO> faultRate = workOrderService.getElevatorBrandFaultRate(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", faultRate, null);
    }

    @GetMapping("/elevator-age-stats")
    public BaseResponse getElevatorAgeStats(@ModelAttribute SearchDTO searchDTO) {
        List<ElevatorAgeStatisticsDTO> stats = workOrderService.getElevatorAgeStatistics(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", stats, null);
    }

    @GetMapping("/elevator-brand-stats")
    public BaseResponse getElevatorBrandStats(@ModelAttribute SearchDTO searchDTO) {
        BrandElevatorStatisticsDTO brandElevatorStats = elevatorInfoService.getBrandElevatorStats(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", brandElevatorStats, null);
    }

    @GetMapping("/month-fault-stats")
    public BaseResponse getMonthFaultStats(@ModelAttribute SearchDTO searchDTO) {
        List<WorkOrderStatisticsDTO> workOrderStatsForMonth = workOrderService.getWorkOrderStatsForMonth(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", workOrderStatsForMonth, null);
    }

    @GetMapping("/order-type-stats")
    public BaseResponse getOrderTypeStats(@ModelAttribute SearchDTO searchDTO) {
        List<OrderTypeStatisticsDTO> statistics = workOrderService.getOrderTypeStatistics(searchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", statistics, null);
    }


    @Autowired
    private IWordExportService wordExportService;

    /*******************************************导出数据******************************************/
    @GetMapping("/export-month-report")
    public BaseResponse exportMonthReport(@ModelAttribute SearchDTO searchDTO) {
        // 获取当前时间并格式化为字符串（包含到分钟）
        String currentTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        wordExportService.generateMonthlyReport(searchDTO, "/tmp/month-" + currentTimeStr + ".docx");
        return new BaseResponse(HttpStatus.OK.value(), "success", null, null);
    }

    @GetMapping("/export-year-report")
    public BaseResponse exportYearReprt(@ModelAttribute SearchDTO searchDTO) {
        // 获取当前时间并格式化为字符串（包含到分钟）
        String currentTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        wordExportService.generateYearlyReport(searchDTO, "/tmp/year-" + currentTimeStr + ".docx");
        return new BaseResponse(HttpStatus.OK.value(), "success", null, null);
    }
}
