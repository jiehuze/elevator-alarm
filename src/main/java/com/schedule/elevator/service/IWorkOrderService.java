package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.*;
import com.schedule.elevator.entity.WorkOrder;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface IWorkOrderService extends IService<WorkOrder> {
    Page<WorkOrder> queryByConditionsPage(SearchDTO wrokOrderDTO);

    List<WorkOrder> queryByConditions(SearchDTO workOrderDTO);

    WorkOrder createWorkOrder(WorkOrder workOrder);

    WorkOrder getWorkOrderByOrderNo(String orderNo);

    Boolean createRescueInfo(WorkOrder workOrder);

    Boolean updateStatus(WorkOrder workOrder);

    Boolean handleWorkOrder(HandleProgressDTO handleProgressDTO);

    Boolean updateByOrderNo(WorkOrder workOrder);

    HashMap<String, DuplicateOrderDTO> getOrderMapByDuplicateRescueCode(SearchDTO searchDTO);

    List<SecondaryFaultStatsDTO> getOrdersByDuplicateRescueCode(SearchDTO searchDTO);

    WorkOrderStatisticsDTO getWorkOrderStatisticsByCondition(SearchDTO searchDTO);

    List<WorkOrderStatisticsDTO> getWorkOrderStatsForMonth(SearchDTO searchDTO);

    List<TimeSlotStatsDTO> getFaultStatsByTimeSlot(SearchDTO searchDTO);

    List<TimeConsumptionStatsDTO> getTimeConsumptionStats(SearchDTO searchDTO);

    RescueLevelStatsDTO getRescueLevelStats(SearchDTO searchDTO);

    ProjectTypeStatItemDTO getProjectTypeStats(SearchDTO searchDTO);

    /**
     * 获取超时工单（到达时间超过30分钟）
     *
     * @param searchDTO 查询条件
     * @return 超时工单列表
     */
    List<OvertimeWorkOrderDTO> getOvertimeWorkOrders(SearchDTO searchDTO);

    List<DistrictStatisticsDTO> getDistrictStatistics(SearchDTO searchDTO);

    List<MaintenanceUnitFaultRateDTO> getMaintenanceUnitFaultRate(@Param("searchDTO") SearchDTO searchDTO);

    List<UsingUnitFaultRateDTO> getUsingUnitFaultRate(@Param("searchDTO") SearchDTO searchDTO);

    List<ElevatorBrandFaultRateDTO> getElevatorBrandFaultRate(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 查询故障率超过50%的电梯品牌
     *
     * @param searchDTO 查询条件
     * @return 故障率超过50%的电梯品牌列表
     */
    String getHighFaultRateBrands(@Param("searchDTO") SearchDTO searchDTO);

    /**
     * 查询同一电梯发生四次以上故障的统计
     *
     * @param searchDTO 查询条件
     * @return 故障次数>=4的电梯列表
     */
    List<RepeatedFaultElevatorDTO> getRepeatedFaultElevators(@Param("searchDTO") SearchDTO searchDTO);

    List<ElevatorAgeStatisticsDTO> getElevatorAgeStatistics(@Param("searchDTO") SearchDTO searchDTO);

    List<OrderTypeStatisticsDTO> getOrderTypeStatistics(@Param("searchDTO") SearchDTO searchDTO);
}