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

    HashMap<String, DuplicateOrderDTO> getOrdersByDuplicateRescueCode(SearchDTO searchDTO);

    WorkOrderStatisticsDTO getWorkOrderStatisticsByCondition(SearchDTO searchDTO);

    List<TimeSlotStatsDTO> getFaultStatsByTimeSlot(SearchDTO searchDTO);

    RescueLevelStatsDTO getRescueLevelStats(SearchDTO searchDTO);

    ProjectTypeStatItemDTO getProjectTypeStats(SearchDTO searchDTO);
}