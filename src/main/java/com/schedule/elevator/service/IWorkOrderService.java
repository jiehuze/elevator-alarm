package com.schedule.elevator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.elevator.dto.*;
import com.schedule.elevator.entity.WorkOrder;

import java.util.HashMap;
import java.util.List;

public interface IWorkOrderService extends IService<WorkOrder> {
    Page<WorkOrder> queryByConditionsPage(SearchDTO wrokOrderDTO);

    WorkOrder createWorkOrder(WorkOrder workOrder);

    Boolean createRescueInfo(WorkOrder workOrder);

    Boolean updateStatus(WorkOrder workOrder);

    Boolean handleWorkOrder(HandleProgressDTO handleProgressDTO);

    Boolean updateByOrderNo(WorkOrder workOrder);

    HashMap<String, DuplicateOrderDTO> getOrderMapByDuplicateRescueCode(SearchDTO searchDTO);

    List<SecondaryFaultStatsDTO> getOrdersByDuplicateRescueCode(SearchDTO searchDTO);

    WorkOrderStatisticsDTO getWorkOrderStatisticsByCondition(SearchDTO searchDTO);

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
}